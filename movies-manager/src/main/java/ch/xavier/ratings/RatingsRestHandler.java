package ch.xavier.ratings;

import ch.xavier.common.ratings.Rating;
import ch.xavier.ratings.repositories.MongoRatingsRepository;
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
public class RatingsRestHandler {
    private final MongoRatingsRepository mongoRatingsRepository;
    private final RatingsService ratingsService;


    @Autowired
    public RatingsRestHandler(MongoRatingsRepository mongoRatingsRepository,
                              RatingsService ratingsService) {
        this.mongoRatingsRepository = mongoRatingsRepository;
        this.ratingsService = ratingsService;
    }

    public Mono<ServerResponse> getAllRatings(ServerRequest request) {
        log.info("Returning all ratings");

        return ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mongoRatingsRepository.findAll()
                        .switchIfEmpty(Mono.error(new RatingNotFoundException())), Rating.class);
    }

    public Mono<ServerResponse> importAllRatingsFromCsv(ServerRequest request) {
        log.info("Beginning import of ratings");

        ratingsService.importRatingsFromAllImporters().subscribe();

        return ok().build();
    }


    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    private static class RatingNotFoundException extends RuntimeException {
    }
}