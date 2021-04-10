package ch.xavier.tags.importers;

import ch.xavier.common.EntitiesImporter;
import ch.xavier.common.tags.Tag;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@ConditionalOnProperty(value = "importer.csv.file")
public class CSVImporter implements EntitiesImporter<Tag> {

    private final String tagsFile;

    @Autowired
    public CSVImporter(@Value("${importer.csv.file}") String tagsFile) {
        this.tagsFile = tagsFile;
    }

    @Override
    public Flux<Tag> importAll() {
        try (Reader reader = Files.newBufferedReader(Path.of(tagsFile));
             CSVReader csvReader = new CSVReader(reader)) {
            return Flux
                    .fromIterable(csvReader.readAll())
                    .map(line -> new Tag(line[2], Long.valueOf(line[1])));
        } catch (IOException e) {
            log.error("Error when reading the csv file:{}, import cancelled", tagsFile, e);
        }

        return Flux.empty();
    }
}