package ch.xavier.rest;

import ch.xavier.common.movies.Movie;
import ch.xavier.movies.MoviesCacheManager;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.util.List;

@Controller
public class MoviesController {

    private static final String URL_PREFIX = "/api";

    @Inject
    private MoviesCacheManager cacheManager;


    @Get(value = URL_PREFIX + "/movie/{movieId}")
    public Mono<Movie> getMovie(@PathVariable String movieId) {
        return cacheManager.find(movieId);
    }

    @Get(value = URL_PREFIX + "/movies")
    public Flux<Movie> getMovies(@QueryValue(value = "id") List<String> movieIds) {
        return cacheManager.findAll(movieIds);
    }

    @Get(value = URL_PREFIX + "/cache/refresh")
    public void refreshCache() {
        cacheManager.importAll().subscribe();
    }

    @Get(value = "/readiness")
    public boolean isCacheReady() {
        return cacheManager.isCacheReady();
    }
}