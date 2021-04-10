package ch.xavier.ratings;

import ch.xavier.ReadinessService;
import ch.xavier.common.EntitiesImporter;
import ch.xavier.common.metrics.MetricsService;
import ch.xavier.common.ratings.Rating;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class RatingsService {

    private final MetricsService metricsService;
    private final RatingsRepository ratingsRepository;
    private final Scheduler scheduler;
    private final List<EntitiesImporter<Rating>> ratingsImporters;
    private final ReadinessService readinessService;

    private final Long retryDelayInMs;
    private final Integer retryAttempts;
    private final Integer timeout;
    private final boolean logEachImport;

    @Autowired
    public RatingsService(MetricsService metricsService,
                          RatingsRepository ratingsRepository,
                          List<EntitiesImporter<Rating>> ratingsImporters,
                          ReadinessService readinessService,
                          @Value("${manager.save.retry.delay.ms}") Long retryDelayInMs,
                          @Value("${manager.save.retry.attempts}") Integer retryAttempts,
                          @Value("${manager.repository.timeout.ms}") Integer timeout,
                          @Value("${manager.imports.logging.enabled}") Boolean logEachImport) {
        this.metricsService = metricsService;
        this.ratingsRepository = ratingsRepository;
        this.ratingsImporters = ratingsImporters;
        this.retryDelayInMs = retryDelayInMs;
        this.retryAttempts = retryAttempts;
        this.timeout = timeout;
        this.readinessService = readinessService;
        this.logEachImport = logEachImport;

        this.scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

        fillRatingsRepositoryIfEmpty();
    }


    private void fillRatingsRepositoryIfEmpty() {
        ratingsRepository.count()
                .doOnNext(count -> {
                    if (count == 0) {
                        log.info("RatingRepository is empty, now filling it from every available importers");
                        importRatingsFromAllImporters().subscribe();
                    } else {
                        log.info("RatingRepository started with {} entries already present", count);
                        readinessService.notifyOneRepositoryReady();
                        metricsService.notifyRatingsImported(count);
                    }
                }).subscribe();
    }

    Flux<Rating> importRatingsFromAllImporters() {
        return ratingsRepository.deleteAll()
                .thenMany(Flux.fromIterable(ratingsImporters)
                        .flatMap(ratingsImporter -> importRatings(ratingsImporter.importAll(),
                                ratingsImporter.getClass().getSimpleName())))
                .doOnComplete(readinessService::notifyOneRepositoryReady);
    }

    private Flux<Rating> importRatings(Flux<Rating> ratings, String importerName) {
        return ratings
                .publishOn(scheduler)
                .flatMap(ratingsRepository::save)
                .doOnNext(response -> metricsService.notifyRatingImported())
                .doOnNext(rating -> logRatingAdded(rating.getUserId(), rating.getMovieId(), importerName))
                .doOnError(e -> {
                    metricsService.notifyRatingImportedError();
                    log.info("Error caught when importing ratings", e);
                })
                .doOnComplete(() -> log.info("All ratings have been imported"));
    }

    Mono<Rating> addRatingToMovie(String rating, String movieId) {
        return ratingsRepository.save(new Rating(rating, "", movieId, new Timestamp(new Date().getTime()).toString()))     //no userId yet
                .timeout(Duration.ofMillis(timeout))
                .retryWhen(Retry.backoff(retryAttempts, Duration.ofMillis(retryDelayInMs)))
                .subscribeOn(scheduler)
                .doOnError(e -> {
                    metricsService.notifyRatingAddedError();
                    log.error("Failure when adding rating:{} to movieId:{}", rating, movieId, e);
                })
                .doOnSuccess(movie -> metricsService.notifyRatingAdded());
    }

    private void logRatingAdded(String userId, String movieId, String importerName) {
        if (logEachImport) {
            log.info("Added a rating from userId:{} for movieId:{} imported from {}", userId, movieId, importerName);
        }
    }
}
