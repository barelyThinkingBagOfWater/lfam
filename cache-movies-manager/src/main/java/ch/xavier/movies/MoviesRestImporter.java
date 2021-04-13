package ch.xavier.movies;

import ch.xavier.common.EntitiesImporter;
import ch.xavier.common.movies.Movie;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.reactor.http.client.ReactorHttpClient;
import io.micronaut.security.oauth2.client.clientcredentials.ClientCredentialsClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class MoviesRestImporter implements EntitiesImporter<Movie> {

    @Value("${importer.rest.uri}")
    private String importerUri;

    @Inject
    private ReactorHttpClient httpClient;

    private final ClientCredentialsClient credentialsClient;
    private final static String TOKEN_SCOPE = "movies:import";


    public MoviesRestImporter(@Named("keycloak") ClientCredentialsClient credentialsClient) {
        this.credentialsClient = credentialsClient;
    }

    public Flux<Movie> getMovies() {
        return Mono.from(credentialsClient.requestToken(TOKEN_SCOPE))
                .map(token ->
                        HttpRequest.GET(importerUri + "/movies")
                        .header("Authorization", "Bearer ".concat(token.getAccessToken())))
                .flatMapMany(request -> httpClient.retrieve(request, Argument.listOf(Movie.class)))
                .flatMap(Flux::fromIterable);
    }

    @Override
    public Flux<Movie> importAll() {
        return getMovies();
    }
}
