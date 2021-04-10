package ch.xavier.movies.repositories;

import ch.xavier.common.movies.Movie;
import ch.xavier.movies.MoviesRepository;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;

@Repository
public class MongoMoviesRepository implements MoviesRepository {

    private final ReactiveMongoTemplate template;

    private static final String DEFAULT_COLLECTION_NAME = "movies";

    @Autowired
    public MongoMoviesRepository(ReactiveMongoTemplate template) {
        this.template = template;
        createUniqueIndex();
    }

    @Override
    public Mono<Movie> save(Movie movie) {
        return template.save(movie);
    }

    @Override
    public Mono<Movie> addTagToMovie(String tag, Long movieId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(movieId));

        Update update = new Update();
        update.push("tags", tag.toLowerCase());

        FindAndModifyOptions options = FindAndModifyOptions.options();
        options.returnNew(true);

        return template.findAndModify(query, update, Movie.class);
    }

    @Override
    public Flux<Movie> findAll() {
        return template.findAll(Movie.class)
                .onBackpressureBuffer(); //useful?
    }

    Mono<Movie> findById(Long movieId) {
        return template.findById(movieId, Movie.class);
    }


    @Override
    public Mono<DeleteResult> deleteAll() {
        return template.remove(Movie.class).all();
    }

    @Override
    public Mono<Long> count() {
        return template.count(new Query(), Movie.class);
    }

    @Override
    public Mono<Long> countTags() {
        return findAll()
                .flatMapIterable(Movie::getTags)
                .distinct()
                .count();
    }

    private Mono<String> createUniqueIndex() {
        return template.createCollection(DEFAULT_COLLECTION_NAME)
                .then(template.indexOps(DEFAULT_COLLECTION_NAME).ensureIndex(
                        new Index().named("uniqueIndex")
                                .on("movieId", Sort.Direction.ASC).unique()))
                .onErrorResume(e -> Mono.empty());
    }
}