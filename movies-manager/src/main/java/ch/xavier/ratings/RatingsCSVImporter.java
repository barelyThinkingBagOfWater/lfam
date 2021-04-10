package ch.xavier.ratings;

import ch.xavier.common.EntitiesImporter;
import ch.xavier.common.ratings.Rating;
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

@Slf4j
@Service
@ConditionalOnProperty(value = "importer.csv.ratings.file")
public class RatingsCSVImporter implements EntitiesImporter<Rating> {

    @Value("${importer.csv.ratings.file}")
    private String ratingsFile;

    @Override
    public Flux<Rating> importAll() {
        try (Reader reader = Files.newBufferedReader(Path.of(ratingsFile));
             CSVReader csvReader = new CSVReader(reader)) {
            return Flux.fromIterable(csvReader.readAll())
                    .map(line -> new Rating(line[2], line[0], line[1], line[3]));
        } catch (IOException e) {
            log.error("Error when reading the file:{}, import cancelled", ratingsFile, e);
        }
        return Flux.empty();
    }
}
