package ch.xavier.ratings;

import ch.xavier.common.ratings.Rating;
import com.mongodb.client.result.DeleteResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RatingsRepository {

    Mono<Rating> findById(Long ratingId);

    Mono<Rating> save(Rating rating);

    Flux<Rating> findAll();

    Mono<DeleteResult> deleteAll();

    Mono<Long> count();
}
