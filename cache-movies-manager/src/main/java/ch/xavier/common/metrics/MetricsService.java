package ch.xavier.common.metrics;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Slf4j
public class MetricsService {

    private final MovieCountersFactory movieCountersFactory;
    private final TagCountersFactory tagCountersFactory;

    @Inject
    public MetricsService(MovieCountersFactory movieCountersFactory, TagCountersFactory tagCountersFactory) {
        this.movieCountersFactory = movieCountersFactory;
        this.tagCountersFactory = tagCountersFactory;
    }

    //TAGS
    public void notifyTagAdded() {
        tagCountersFactory.getTagsAddedCounter().increment();
    }

    public void notifyTagAddedError() {
        tagCountersFactory.getTagsAddedErrorsCounter().increment();
    }

    //MOVIES
    public void notifyMovieImported() {
        movieCountersFactory.getMoviesImportedCounter().increment();
    }

    public void notifyMovieImportedError() {
        movieCountersFactory.getMoviesImportedErrorsCounter().increment();
    }

    public void notifyMovieSearched() {
        movieCountersFactory.getMoviesSearchedCounter().increment();
    }
}