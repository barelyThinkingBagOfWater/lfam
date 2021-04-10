package ch.xavier.tags;

import ch.xavier.common.EntitiesImporter;
import ch.xavier.common.metrics.MetricsService;
import ch.xavier.common.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
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

/**
 * Should be made generic enough to be used with other repo/entities
 */
@Service
@Slf4j
public class TagsCacheManager {

    private final TagsRepository repository;
    private final MetricsService metricsService;
    private final List<EntitiesImporter<Tag>> importers;
    private final Scheduler scheduler;

    private final Long retryDelayInMs;
    private final Integer retryAttempts;
    private final Integer timeout;
    private boolean isCacheReady = false;
    private final boolean logEachImport;


    @Autowired
    public TagsCacheManager(TagsRepository repository,
                            MetricsService metricsService,
                            List<EntitiesImporter<Tag>> importers,
                            @Value("${manager.retry.delay.ms}") Long retryDelayInMs,
                            @Value("${manager.retry.attempt}") Integer retryAttempts,
                            @Value("${manager.repository.timeout.ms}") Integer repositoryTimeout,
                            @Value("${manager.imports.logging.enabled}") Boolean logEachImport) {
        this.repository = repository;
        this.metricsService = metricsService;
        this.importers = importers;
        this.retryDelayInMs = retryDelayInMs;
        this.retryAttempts = retryAttempts;
        this.timeout = repositoryTimeout;
        this.logEachImport = logEachImport;

        this.scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()));

        fillCacheIfEmpty(repository);
    }


    private void fillCacheIfEmpty(TagsRepository repository) {
        repository.count()
                .doOnNext(count -> {
                    if (count.equals(0L)) {
                        log.info("Cache is empty, now filling it from every available importers");
                        importAll().subscribe();
                    } else {
                        log.info("Cache started but already filled with {} entries", count);
                        isCacheReady = true;
                    }
                }).subscribe();
    }

    public Flux<DocWriteResponse> importAll() {
        return emptyCache()
                .thenMany(importTagsFromAllImporters())
                .doOnComplete(() -> isCacheReady = true);
    }

    private Flux<DocWriteResponse> importTagsFromAllImporters() {
        return Flux.fromIterable(importers)
                .flatMap(importer -> importTags(importer.importAll()));
    }

    private Flux<UpdateResponse> importTags(Flux<Tag> tags) {
        return tags
                .flatMap(tag -> addMovieIdsToTag(tag.getMovieIds(), tag.getTagName()))
                .doOnNext(response -> metricsService.notifyTagImported())
                .publishOn(scheduler) //so exit(1) works
                .doOnError(e -> {
                    metricsService.notifyTagImportedError();
                    log.error("Error caught when importing tags, exiting so that Kubernetes can restart the cache (with its exponential back-off delay):", e);
                    System.exit(1);
                })
                .doOnComplete(() -> log.info("Import successful, have fun with the tags!"));
    }

    private Mono<BulkByScrollResponse> emptyCache() {
        return repository.empty();
    }

    Flux<UpdateResponse> addMovieIdsToTags(Set<Long> movieIds, Set<String> tags) {
        return Flux.fromIterable(tags)
                .flatMap(tag -> addMovieIdsToTag(movieIds, tag))
                .doOnNext(tag -> metricsService.notifyTagAdded())
                .doOnError(tag -> metricsService.notifyTagAddedError());
    }

    public Flux<UpdateResponse> addMovieIdsToTag(Set<Long> movieIds, String tag) {
        return Flux.fromIterable(movieIds)
                .publishOn(scheduler)
                .flatMap(movieId -> addMovieIdToExistingOrNewTag(movieId, tag));
    }

    private Mono<UpdateResponse> addMovieIdToExistingOrNewTag(Long movieId, String tagName) {
        return repository.addTagToMovie(tagName, movieId)
                .doOnNext(response -> logImport(tagName, movieId))
                .timeout(Duration.ofMillis(timeout))
                .retryWhen(Retry.backoff(retryAttempts, Duration.ofMillis(retryDelayInMs)))
                .doOnError(e -> log.error("Error when adding movieId:{} to tag:{}", movieId, tagName, e));
    }


    private void logImport(String tag, Long movieId) {
        if (logEachImport) {
            log.info("Tag:{} imported for movieId:{}", tag, movieId);
        }
    }

    public Flux<Tag> fuzzilyFind(String tag) {
        metricsService.notifyTagSearched();

        return repository
                .fuzzilyFind(tag); //add a subscribeOn here? Load tests to see if useful
    }

    public boolean isCacheReady() {
        return isCacheReady;
    }
}
