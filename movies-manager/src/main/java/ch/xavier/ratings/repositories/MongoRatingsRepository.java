package ch.xavier.ratings.repositories;

import ch.xavier.common.ratings.Rating;
import ch.xavier.ratings.RatingsRepository;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MongoRatingsRepository implements RatingsRepository {

    private final ReactiveMongoTemplate template;

    private static final String RATINGS_COLLECTION_NAME = "ratings";

    @Autowired
    public MongoRatingsRepository(ReactiveMongoTemplate template) {
        this.template = template;
        createUniqueIndexOnUserIdAndMovieId();
    }

    @Override
    public Mono<Rating> findById(Long ratingId) {
        return template.findById(ratingId, Rating.class);
    }

    @Override
    public Mono<Rating> save(Rating rating) {
        return template.save(rating);
    }

    @Override
    public Flux<Rating> findAll() {

        return template.findAll(Rating.class);
    }

    @Override
    public Mono<DeleteResult> deleteAll() {
        return template.remove(Rating.class).all();
    }

    @Override
    public Mono<Long> count() {
        return template.count(new Query(), Rating.class);
    }

    private Mono<String> createUniqueIndexOnUserIdAndMovieId() {
        return template.createCollection(RATINGS_COLLECTION_NAME)
                .then(template.indexOps(RATINGS_COLLECTION_NAME).ensureIndex(
                        new Index().named("uniqueIndex")
                                .on("userId", Sort.Direction.ASC)
                                .on("movieId", Sort.Direction.ASC).unique()))
                .onErrorResume(e -> Mono.empty());
    }
}
