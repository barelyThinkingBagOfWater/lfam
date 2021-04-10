package ch.xavier.tags.importers;

import ch.xavier.common.EntitiesImporter;
import ch.xavier.common.movies.Movie;
import ch.xavier.common.tags.Tag;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Service
@ConditionalOnProperty("importer.rest.uri")
public class RestImporter implements EntitiesImporter<Tag> {

    @Autowired
    private WebClient client;



    private Flux<Movie> getMovies() {
        return client.get().uri("/movies")
                .retrieve()
                .bodyToFlux(Movie.class);
    }

    @Override
    public Flux<Tag> importAll() {
        return getMovies()
                .flatMap(movie -> Flux.fromIterable(movie.getTags())
                        .map(tag -> new Tag(tag, movie.getMovieId())));
    }
}
