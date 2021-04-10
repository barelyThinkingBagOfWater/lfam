package ch.xavier.tags.manager;

import ch.xavier.common.tags.Tag;
import ch.xavier.tags.TagsCacheManager;
import ch.xavier.tags.repositories.ElasticRestClient;
import ch.xavier.tags.repositories.ElasticTagsRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
        "elastic.host=172.18.42.5",
        "importer.rest.uri=http://172.18.42.5",
        "spring.rabbitmq.host=172.18.42.4"})
@Slf4j
public class ITestTagsCacheManager {

    @Autowired
    private ElasticTagsRepository tagsRepository;
    @Autowired
    private TagsCacheManager tagsCacheManager;

    @Before
    public void setUp() {
        tagsRepository.removeIndex(ElasticRestClient.DEFAULT_INDEX_NAME)
                .then(tagsRepository.createIndex(ElasticRestClient.DEFAULT_INDEX_NAME))
                .block();
    }


    @Test
    public void addMovieIdToTag_updateTheTag_GivenAnExistingMovie() {
        // GIVEN
        String tagName = "tag";
        Set<Long> startingMovieIds = Set.of(1L);
        Set<Long> newMovieIds = Set.of(4L);
        Set<Long> mergedSets = Set.of(1L, 4L);

        Tag tag = new Tag(tagName, startingMovieIds);

        // WHEN
        tagsRepository.save(tag)
                .thenMany(tagsCacheManager.addMovieIdsToTag(newMovieIds, tagName))

                // THEN
                .then(tagsRepository.find(tagName))
                .doOnNext(newTag -> assertEquals(newTag.getMovieIds(), mergedSets))
                .block();
    }

    @Test
    public void addMovieIdToTag_updateTheTag_GivenNoExistingMovie() {
        // GIVEN
        String tagName = "tag";
        Set<Long> movieIds = Set.of(4L);

        // WHEN
        tagsCacheManager.addMovieIdsToTag(movieIds, tagName)

                //THEN
                .then(tagsRepository.find(tagName))
                .doOnNext(newTag -> assertEquals(newTag.getTagName(), tagName))
                .doOnNext(newTag -> assertEquals(newTag.getMovieIds(), movieIds))
                .block();
    }
}
