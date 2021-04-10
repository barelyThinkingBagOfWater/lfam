package ch.xavier.rest;

import ch.xavier.ReadinessService;
import ch.xavier.movies.MoviesRestHandler;
import ch.xavier.ratings.RatingsRestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Slf4j
@Configuration
public class RestResource {
    
    @Bean
    public RouterFunction<ServerResponse> route(MoviesRestHandler moviesRestHandler, RatingsRestHandler ratingsRestHandler,
                                                ReadinessService readinessService) {
        return RouterFunctions
                .route(GET("/movies"), moviesRestHandler::getAllMovies)
                .andRoute(GET("/movies/import"), moviesRestHandler::importAllMoviesAndTagsFromCsv)
                .andRoute(GET("/ratings"), ratingsRestHandler::getAllRatings)
                .andRoute(GET("/ratings/import"), ratingsRestHandler::importAllRatingsFromCsv)
                .andRoute(GET("/readiness"), readinessService::areRepositoriesReady);
    }
}