package ch.xavier.movies.repositories;

import ch.xavier.common.movies.Movie;
import io.lettuce.core.Value;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.reactive.RedisServerReactiveCommands;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;


@Singleton
public class RedisMoviesRepository {

    @Inject
    private RedisReactiveCommands<String, Movie> reactiveRedisOperations;
    @Inject
    private RedisServerReactiveCommands<String, Movie> serverReactiveCommands;

    public Mono<String> save(Movie movie) {
        return reactiveRedisOperations.set(movie.getMovieId().toString(), movie);
    }

    public Mono<Movie> find(String movieId) {
        return reactiveRedisOperations.get(movieId);
    }

    public Flux<Movie> findAll(List<String> movieIds) {
        return reactiveRedisOperations.mget(movieIds.toArray(new String[0]))
                .map(Value::getValue);
    }

    public Mono<String> addTagToMovie(String tag, String movieId) {
        return find(movieId)
                .switchIfEmpty(Mono.error(new MissingMovieException()))
                .flatMap(movie -> save(movie.withNewTag(tag)));
    }

    public Mono<Long> count() {
        return serverReactiveCommands.dbsize();
    }

    public Mono<String> empty() {
        return serverReactiveCommands.flushallAsync();
    }

    private static class MissingMovieException extends RuntimeException { }
}
