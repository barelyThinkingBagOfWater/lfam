package ch.xavier.movies;

import ch.xavier.movies.repositories.MoviesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MoviesCacheManager {

    private final MoviesRestImporter importer;
    private final MoviesRepository repository;


    @Autowired
    public MoviesCacheManager(MoviesRestImporter importer, MoviesRepository repository) {
        this.importer = importer;
        this.repository = repository;
    }



    public void importAllMovies() {
        List<Movie> movies = importer.importAllMovies();

        log.info("movies size:{}", movies.size());

        repository.saveAll(movies);


        log.info("Done, size in db:{}", repository.count());
    }
}
