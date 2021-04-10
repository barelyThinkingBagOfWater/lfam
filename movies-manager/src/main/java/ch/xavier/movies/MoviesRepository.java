package ch.xavier.movies;

import ch.xavier.common.movies.Movie;
import com.mongodb.client.result.DeleteResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MoviesRepository {
    Mono<Movie> save(Movie movie);

    Mono<Movie> addTagToMovie(String tag, Long movieId);

    Flux<Movie> findAll();

    Mono<DeleteResult> deleteAll();

    Mono<Long> count();

    Mono<Long> countTags();
}