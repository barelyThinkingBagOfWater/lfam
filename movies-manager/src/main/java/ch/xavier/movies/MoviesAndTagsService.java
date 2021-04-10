package ch.xavier.movies;

import ch.xavier.ReadinessService;
import ch.xavier.common.EntitiesImporter;
import ch.xavier.common.metrics.MetricsService;
import ch.xavier.common.movies.Movie;
import ch.xavier.common.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class MoviesAndTagsService {

    private final MetricsService metricsService;
    private final MoviesRepository moviesRepository;
    private final Scheduler scheduler;
    private final List<EntitiesImporter<Movie>> moviesImporters;
    private final List<EntitiesImporter<Tag>> tagsImporters;
    private final ReadinessService readinessService;

    private final Long retryDelayInMs;
    private final Integer retryAttempts;
    private final Integer timeout;
    private final Boolean logEachImport;


    @Autowired
    public MoviesAndTagsService(MetricsService metricsService,
                                MoviesRepository moviesRepository,
                                List<EntitiesImporter<Movie>> moviesImporters,
                                List<EntitiesImporter<Tag>> tagsImporters,
                                ReadinessService readinessService,
                                @Value("${manager.save.retry.delay.ms}") Long retryDelayInMs,
                                @Value("${manager.save.retry.attempts}") Integer retryAttempts,
                                @Value("${manager.repository.timeout.ms}") Integer timeout,
                                @Value("${manager.imports.logging.enabled}") Boolean logEachImport) {
        this.metricsService = metricsService;
        this.moviesRepository = moviesRepository;
        this.tagsImporters = tagsImporters;
        this.moviesImporters = moviesImporters;
        this.readinessService = readinessService;
        this.retryDelayInMs = retryDelayInMs;
        this.retryAttempts = retryAttempts;
        this.timeout = timeout;
        this.logEachImport = logEachImport;

        this.scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

        fillRepositoriesWithMoviesAndTagsIfEmpty();
    }


    private void fillRepositoriesWithMoviesAndTagsIfEmpty() {
        moviesRepository.count()
                .doOnNext(count -> {
                    if (count == 0) {
                        log.info("MovieRepository is empty, now filling it with movies and tags from every available importers");
                        importAllMoviesAndTags().subscribe();
                    } else {
                        log.info("MovieRepository started with {} entries already present", count);
                        readinessService.notifyOneRepositoryReady(); //movies
                        readinessService.notifyOneRepositoryReady(); //tags

                        metricsService.notifyMoviesImported(count);
                        moviesRepository.countTags()
                                .doOnNext(tagsCount -> {
                                    log.info("MovieRepository already includes {} distinct tags", tagsCount);
                                    metricsService.notifyTagsImported(tagsCount);
                                }).subscribe();
                    }
                }).subscribe();
    }

    Flux<Movie> importAllMoviesAndTags() {
        return moviesRepository.deleteAll()
                .thenMany(importMoviesFromAllImporters())
                .thenMany(importTagsFromAllImporters())
                .doOnComplete(() -> log.info("All movies and tags have been imported"));
    }

    private Flux<Movie> importTagsFromAllImporters() {
        return Flux.fromIterable(tagsImporters)
                .flatMap(tagsImporter -> importTags(tagsImporter.importAll())
                        .doOnNext(movie -> logTagAdded(movie.getMovieId(), tagsImporter.getClass().getSimpleName())));
    }

    private Flux<Movie> importMoviesFromAllImporters() {
        return Flux.fromIterable(moviesImporters)
                .flatMap(moviesImporter -> importMovies(moviesImporter.importAll(), moviesImporter.getClass().getSimpleName()));
    }

    private Flux<Movie> importMovies(Flux<Movie> movies, String importerName) {
        return movies
                .publishOn(scheduler)
                .flatMap(moviesRepository::save)
                .doOnNext(movie -> logMovieImport(movie.toString(), importerName))
                .doOnNext(movie -> metricsService.notifyMovieImported())
                .doOnError(e -> {
                    metricsService.notifyMovieImportedError();
                    log.info("Error caught when importing movies", e);
                })
                .doOnComplete(readinessService::notifyOneRepositoryReady);
    }


    private Flux<Movie> importTags(Flux<Tag> tags) {
        return tags
                .flatMap(tag -> addTagToMovies(tag.getTagName(), tag.getMovieIds()))
                .doOnNext(tag -> metricsService.notifyTagImported())
                .doOnError(e -> {
                    log.info("Error caught when importing tags", e);
                    metricsService.notifyTagImportedError();
                })
                .doOnComplete(readinessService::notifyOneRepositoryReady);
    }

    public Flux<Movie> addTagsToMovies(Set<String> tags, Set<Long> movieIds) {
        return Flux.fromIterable(tags)
                .flatMap(tag -> addTagToMovies(tag, movieIds))
                .doOnNext(tag -> metricsService.notifyTagAdded())
                .doOnError(tag -> metricsService.notifyTagAddedError());
    }

    private Flux<Movie> addTagToMovies(String tagName, Set<Long> movieIds) {
        return Flux.fromIterable(movieIds)
                .flatMap(movieId -> addTagToMovie(tagName, movieId));
    }

    private Mono<Movie> addTagToMovie(String tag, Long movieId) {
        return moviesRepository.addTagToMovie(tag, movieId)
                .timeout(Duration.ofMillis(timeout))
                .retryWhen(Retry.backoff(retryAttempts, Duration.ofMillis(retryDelayInMs)))
                .subscribeOn(scheduler) //use a different Scheduler here?
                .doOnError(e -> log.error("Failure when adding tag:{} to movieId:{}", tag, movieId, e));
    }

    private void logMovieImport(String movieName, String importerName) {
        if (logEachImport) {
            log.info("Movie:{} imported from {}", movieName, importerName);
        }
    }

    private void logTagAdded(Long movieId, String importerName) {
        if (logEachImport) {
            log.info("Added a tag for movieId:{} imported from {}", movieId, importerName);
        }
    }
}
