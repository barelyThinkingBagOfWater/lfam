package ch.xavier.movies;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class MoviesRestController {

    private static final String URL_PREFIX = "/api";

    @Autowired
    private MoviesCacheManager manager;


    @GetMapping(URL_PREFIX + "/movies")
    public List<Movie> getAllMovies(@RequestParam(name = "id") String movieIds) {

        log.info("movieIds:{}", movieIds);

        return null;
    }

    @GetMapping(URL_PREFIX + "/movie/{movieId}")
    public Movie getMovie(@PathVariable String movieId) {
        log.info("Movie id:{} requested:", movieId);

        return null;
    }

    @GetMapping("/cache/refresh")
    public void refreshCache() {
        log.info("Importing now movies");

        manager.importAllMovies();
    }
}