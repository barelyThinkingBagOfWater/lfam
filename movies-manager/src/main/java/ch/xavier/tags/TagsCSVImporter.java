package ch.xavier.tags;

import ch.xavier.common.EntitiesImporter;
import ch.xavier.common.tags.Tag;
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
@ConditionalOnProperty(value = "importer.csv.tags.file")
public class TagsCSVImporter implements EntitiesImporter<Tag> {

    @Value("${importer.csv.tags.file}")
    private String tagsFile;

    @Override
    public Flux<Tag> importAll() {
        try (Reader reader = Files.newBufferedReader(Path.of(tagsFile));
             CSVReader csvReader = new CSVReader(reader)) {

            return Flux.fromIterable(csvReader.readAll())
                    .map(line -> new Tag(line[2].toLowerCase(), Long.valueOf(line[1])));
        } catch (IOException e) {
            log.error("Error when reading the file:{}, import cancelled", tagsFile, e);
        }
        return Flux.empty();
    }
}
