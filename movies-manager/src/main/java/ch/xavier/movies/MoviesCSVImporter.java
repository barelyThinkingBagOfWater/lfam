package ch.xavier.movies;

import ch.xavier.common.EntitiesImporter;
import ch.xavier.common.movies.Movie;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Collections.emptySet;

@Slf4j
@Service
@ConditionalOnProperty(value = "importer.csv.movies.file")
public class MoviesCSVImporter implements EntitiesImporter<Movie> {

    @Value("${importer.csv.movies.file}")
    private String moviesFile;


    public Flux<Movie> importAll() {
        try (Reader reader = Files.newBufferedReader(Path.of(moviesFile));
             CSVReader csvReader = new CSVReader(reader)) {

            return Flux.fromIterable(csvReader.readAll())
                    .map(line -> new Movie(Long.valueOf(line[0]), line[1], line[2], emptySet()));
        } catch (IOException e) {
            log.error("Error when reading the file:{}, import cancelled", moviesFile, e);
        }
        return Flux.empty();
    }
}
