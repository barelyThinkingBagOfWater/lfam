package ch.xavier.movies.repositories;

import ch.xavier.common.movies.Movie;
import ch.xavier.movies.MoviesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;


@Repository
public class RedisMoviesRepository implements MoviesRepository {

    private final ReactiveRedisOperations<String, Movie> reactiveRedisOperations;
    private final ReactiveValueOperations<String, Movie> reactiveValueOps;

    @Autowired
    public RedisMoviesRepository(ReactiveRedisTemplate<String, Movie> template) {
        this.reactiveRedisOperations = template;
        this.reactiveValueOps = template.opsForValue();
    }

    @Override
    public Mono<Boolean> save(Movie movie) {
        return reactiveValueOps.set(movie.getMovieId().toString(), movie);
    }

    @Override
    public Mono<Movie> find(String movieId) {
        return reactiveValueOps.get(movieId);
    }

    @Override
    public Mono<List<Movie>> findAll(List<String> movieIds) {
        return reactiveValueOps.multiGet(movieIds);
    }

    @Override
    public Mono<Boolean> addTagToMovie(String tag, String movieId) {
        return find(movieId)
                .switchIfEmpty(Mono.error(new MissingMovieException()))
                .flatMap(movie -> save(movie.withNewTag(tag)));
    }

    @Override
    public Mono<Long> count() {
        return reactiveRedisOperations.scan().count();
    }

    @Override
    public Mono<Long> empty() {
        return reactiveRedisOperations.delete(reactiveRedisOperations.scan());
    }

    private static class MissingMovieException extends RuntimeException { }
}
