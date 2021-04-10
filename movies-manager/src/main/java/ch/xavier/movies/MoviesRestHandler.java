package ch.xavier.movies;

import ch.xavier.common.movies.Movie;
import ch.xavier.movies.repositories.MongoMoviesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@Slf4j
public class MoviesRestHandler {

    private final MongoMoviesRepository moviesRepository;
    private final MoviesAndTagsService moviesAndTagsService;


    @Autowired
    public MoviesRestHandler(MongoMoviesRepository moviesRepository,
                             MoviesAndTagsService moviesAndTagsService) {
        this.moviesRepository = moviesRepository;
        this.moviesAndTagsService = moviesAndTagsService;
    }


//    public Mono<ServerResponse> getMovie(ServerRequest request) { //Do I keep it?
//        String movieId = request.pathVariable("id");
//
//        log.info("Getting movie id:{}", movieId);
//
//        return ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(moviesAndTagsService.findById(movieId)
//                                .switchIfEmpty(Mono.error(new MovieNotFoundException())),
//                        Movie.class);
//    }

    public Mono<ServerResponse> getAllMovies(ServerRequest request) {
        return ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(moviesRepository.findAll()
                        .doOnComplete(() -> log.info("Movies have been served to a cache")), Movie.class);
    }

    public Mono<ServerResponse> importAllMoviesAndTagsFromCsv(ServerRequest request) {
        return moviesAndTagsService.importAllMoviesAndTags()
                .doOnComplete(() -> log.info("Import of movies and tags from csv files done"))
                .then(ok().build());
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    private static class MovieNotFoundException extends RuntimeException {
    }
}