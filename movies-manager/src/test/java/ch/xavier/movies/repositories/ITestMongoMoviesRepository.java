package ch.xavier.movies.repositories;

import ch.xavier.common.movies.Movie;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * These tests remain there to be run automatically as a generic integration tests
 * using the failsafe and docker plugins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        ReactiveMongoMoviesTestConfig.class,
        MongoMoviesRepository.class
})
public class ITestMongoMoviesRepository {

    @Autowired
    private MongoMoviesRepository moviesRepository;

    @Before
    public void setUp() {
        moviesRepository.deleteAll().block();
    }


    @Test
    public void save1Movie() {
        //GIVEN
        Movie movieToSave = new Movie(1L, "title", "genres", Set.of("tag1"));
        moviesRepository.save(movieToSave)

                // WHEN
                .then(moviesRepository.findById(1L))

                // THEN
                .doOnNext(Assertions::assertNotNull)
                .doOnNext(movie -> assertEquals(movie.getTags(), movieToSave.getTags()))
                .doOnNext(movie -> assertEquals(movie.getGenres(), movieToSave.getGenres()))
                .doOnNext(movie -> assertEquals(movie.getMovieId(), movieToSave.getMovieId()))
                .doOnNext(movie -> assertEquals(movie.getTitle(), movieToSave.getTitle()))
                .block();
    }

    @Test
    public void addTagToMovie() {
        //BEFORE
        String oldTag = "tag1";
        Movie movieToSave = new Movie(1L, "title", "genres", Set.of(oldTag));
        String newTag = "tag2";

        moviesRepository.save(movieToSave)
                .then(moviesRepository.addTagToMovie(newTag, 1L))

                // WHEN
                .then(moviesRepository.findById(1L))

                // THEN
                .doOnNext(Assertions::assertNotNull)
                .doOnNext(movie -> assertEquals(movie.getTags(), Set.of(oldTag, newTag)))
                .doOnNext(movie -> assertEquals(movie.getGenres(), movieToSave.getGenres()))
                .doOnNext(movie -> assertEquals(movie.getMovieId(), movieToSave.getMovieId()))
                .doOnNext(movie -> assertEquals(movie.getTitle(), movieToSave.getTitle()))
                .block();
    }

    @Test
    public void findAllMovies() {
        //BEFORE
        Movie movieToSave = new Movie(1L, "title", "genres", Set.of("tag1"));
        Movie movieToSave2 = new Movie(2L, "title2", "genres2", Set.of("tag2"));

        List<Movie> movies = moviesRepository.save(movieToSave)
                .then(moviesRepository.save(movieToSave2))

                // WHEN
                .thenMany(moviesRepository.findAll())
                .collectList()
                .block();

        // THEN
        assertEquals(movies.get(0).getTags(), movieToSave.getTags());
        assertEquals(movies.get(0).getGenres(), movieToSave.getGenres());
        assertEquals(movies.get(0).getMovieId(), movieToSave.getMovieId());
        assertEquals(movies.get(0).getTitle(), movieToSave.getTitle());

        assertEquals(movies.get(1).getTags(), movieToSave2.getTags());
        assertEquals(movies.get(1).getGenres(), movieToSave2.getGenres());
        assertEquals(movies.get(1).getMovieId(), movieToSave2.getMovieId());
        assertEquals(movies.get(1).getTitle(), movieToSave2.getTitle());
    }
}
