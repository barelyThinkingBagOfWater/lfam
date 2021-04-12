package ch.xavier.rest;

import ch.xavier.common.movies.Movie;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class MoviesController {

    private static final String URL_PREFIX = "/api";


    @Get(value = URL_PREFIX + "/movie/{movieId}")
    public Mono<Movie> getMovie(String movieId) {
        return null;
    }

    @Get(value = URL_PREFIX + "/movies")
    public Flux<Movie> getMovies() {
        return null;
    }

    @Get(value = URL_PREFIX + "/cache/refresh")
    public void refreshCache() {
    }

    @Get(value = "/readiness")
    public boolean isCacheReady() {
        return false;
    }
}