package ch.xavier.movies.repositories;


import ch.xavier.common.movies.Movie;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * For now the repositories connect to a redis instance on localhost so you need to start one using docker, simply run :
 * docker run --rm -d --name redis -p 6379:6379 redis:6
 *
 * Compression cannot really be tested right now  as the generated payload of the TestEntities that are being saved in DB
 * are randomized. Once we have the real entities with some names of cities and such we'll have enough repetitions
 * to actually make use of the compression
 **/
@Slf4j
@MicronautTest
@Property(name = "micronaut.server.port", value = "8080")
@Property(name = "redis.single.url", value = "localhost")
class MoviesRepositoryTest {

    @Inject
    private RedisMoviesRepository repository;


    @BeforeEach
    public void setUp() {
        repository.empty().block();
    }

    @Test
    public void saveMovie_correctlySavesAndFindsMovie() {
        // GIVEN
        Long movieId = 1L;
        String title = "title";
        String genre = "genre";
        Set<String> tags = Set.of("tag1", "tag2");

        Movie movie = new Movie(movieId, title, genre, tags);

        // WHEN
        repository.save(movie).block();
        Movie receivedMovie = repository.find(movieId.toString()).block();

        // THEN
        assertEquals(1, repository.count().block());
        assertEquals(title, receivedMovie.getTitle());
        assertEquals(genre, receivedMovie.getGenres());
        assertEquals(tags, receivedMovie.getTags());
    }


    @Test
    public void findAllMovies_fetchesAllMovies() {
        // GIVEN
        Long movieId = 1L;
        String title = "title";
        String genre = "genre";
        Set<String> tags = Set.of("tag1", "tag2");

        Long movieId2 = 2L;
        String title2 = "title2";
        String genre2 = "genre2";
        Set<String> tags2 = Set.of("tag3", "tag4");

        Movie movie = new Movie(movieId, title, genre, tags);
        Movie movie2 = new Movie(movieId2, title2, genre2, tags2);

        repository.save(movie).block();
        repository.save(movie2).block();

        // WHEN
        List<Movie> movies = repository.findAll(List.of(movieId.toString(), movieId2.toString())).collectList().block();


        // THEN
        assertEquals(2, repository.count().block());
        assertEquals(title, movies.get(0).getTitle());
        assertEquals(genre, movies.get(0).getGenres());
        assertEquals(tags, movies.get(0).getTags());
        assertEquals(title2, movies.get(1).getTitle());
        assertEquals(genre2, movies.get(1).getGenres());
        assertEquals(tags2, movies.get(1).getTags());
    }
}