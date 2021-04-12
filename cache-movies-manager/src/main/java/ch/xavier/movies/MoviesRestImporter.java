package ch.xavier.movies;

import ch.xavier.common.EntitiesImporter;
import ch.xavier.common.movies.Movie;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.reactor.http.client.ReactorHttpClient;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@Slf4j
public class MoviesRestImporter implements EntitiesImporter<Movie> {

    @Value("${importer.rest.uri}")
    private String importerUri;

    @Inject
    private ReactorHttpClient httpClient;

    public Flux<Movie> getMovies() {
        HttpRequest<?> request = HttpRequest.GET(importerUri + "/movies");

        return httpClient.retrieve(request, Argument.listOf(Movie.class))
                .flatMap(Flux::fromIterable); //better way?
    }

    @Override
    public Flux<Movie> importAll() {
        return getMovies();
    }
}
