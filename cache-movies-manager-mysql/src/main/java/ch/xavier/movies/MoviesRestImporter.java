package ch.xavier.movies;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.util.List;

@Service
@ConditionalOnProperty("importer.rest.uri")
public class MoviesRestImporter {

    private final WebClient client;

    @Autowired
    public MoviesRestImporter(@Value("${importer.rest.uri}") String uri) throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE) //not for prod...
                .build();

        HttpClient httpClient = HttpClient
                .create()
                .secure(sslSpec -> sslSpec.sslContext(sslContext));

        client = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(uri)
                .build();
    }


    public List<Movie> importAllMovies() {
        return client.get().uri("/movies")
                .retrieve()
                .bodyToFlux(Movie.class)
                .collectList()
                .block();
    }
}
