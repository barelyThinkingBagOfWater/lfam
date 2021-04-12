package ch.xavier.movies;

import ch.xavier.common.movies.Movie;
import io.micronaut.context.annotation.Value;
import io.micronaut.reactor.http.client.ReactorHttpClient;
import reactor.core.publisher.Flux;

import javax.inject.Singleton;

@Singleton
public class MoviesRestImporter {

    @Value("${importer.rest.uri}")
    private String importerUri;

    private ReactorHttpClient httpClient;

    public Flux<Movie> getMovies() {
        return httpClient.retrieve(importerUri + "/movies")
                .cast(Movie.class);
    }
}
