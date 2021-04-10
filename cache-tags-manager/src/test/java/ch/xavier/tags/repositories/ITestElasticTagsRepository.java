package ch.xavier.tags.repositories;

import ch.xavier.common.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
        "elastic.host=172.18.42.5",
        "importer.rest.uri=http://172.18.42.4",
        "spring.rabbitmq.host=172.18.42.3"})
@Slf4j
public class ITestElasticTagsRepository {

    @Autowired
    private ElasticTagsRepository tagsRepository;

    @Before
    public void setUp() {
        tagsRepository.removeIndex(ElasticRestClient.DEFAULT_INDEX_NAME)
                .then(tagsRepository.createIndex(ElasticRestClient.DEFAULT_INDEX_NAME))
                .block();
    }

    @Test
    public void saving_one_Tag_saves_tag_as_expected() {
        // GIVEN
        String tagName = "asdfghjk";
        tagsRepository.save(new Tag(tagName))

                // WHEN
                .then(tagsRepository.find(tagName))

                // THEN
                .doOnNext(Assertions::assertNotNull)
                .doOnNext(tag -> assertEquals(tag.getTagName(), tagName))
                .block();
    }

    @Test
    public void saving_hundred_Tags_saves_tags_as_expected() {
        // GIVEN
        Long numberSent = 100L;

        // WHEN
        Flux.fromIterable(generateRandomTags(numberSent))
                .flatMap(tagsRepository::save)

                // THEN
                .count()
                .doOnNext(next -> waitOneSecond())
                .then(tagsRepository.count())
                .doOnNext(count -> assertEquals(count, numberSent))
                .block();
    }


    @Test
    public void second_level_fuzzilyFind_works_as_expected() {
        // WHEN
        String tagName1 = "tga1";
        String tagName2 = "tag11";
        String tagName3 = "tag111";
        Long expectedCount = 2L;

        tagsRepository.save(new Tag(tagName1))
                .then(tagsRepository.save(new Tag(tagName2)))
                .then(tagsRepository.save(new Tag(tagName3)))
                .doOnNext(next -> waitOneSecond())

                // WHEN
                .then(tagsRepository.fuzzilyFind("tag").count())

                // THEN
                .doOnNext(count -> assertEquals(expectedCount, count))
                .block();
    }

    @Test
    public void addTagToMovie_creates_new_tag_as_expected() {
        // GIVEN1
        String tagName = "updatedTag1";
        Long movieId = 1L;

        Tag tag1 = new Tag(tagName, Set.of(movieId));

        //GIVEN2
        String tagToAdd = "updatedTag2";

        tagsRepository.save(tag1)
                .then(tagsRepository.find(tagName))

                // THEN1
                .doOnNext(tag -> assertEquals(tag.getTagName(), tag1.getTagName()))
                .doOnNext(tag -> assertEquals(tag.getMovieIds(), tag1.getMovieIds()))

                // WHEN2
                .then(tagsRepository.addTagToMovie(tagToAdd, movieId))
                .thenMany(tagsRepository.find(tagToAdd))

                // THEN2
                .doOnNext(tag -> assertEquals(tag.getTagName(), tagToAdd))
                .doOnNext(tag -> assertEquals(tag.getMovieIds(), Set.of(movieId)))
                .blockLast();
    }

    @Test
    public void findTag_creates_new_tag_if_missing() {
        // GIVEN
        String tagName = "missingTag";

        // WHEN
        tagsRepository.find(tagName)

                // THEN
                .doOnNext(tag -> assertEquals(tag.getTagName(), tagName))
                .doOnNext(tag -> assertEquals(tag.getMovieIds(), Collections.emptySet()))
                .block();
    }

    @Test
    public void getVersionedTag_creates_new_tag_if_missing() {
        // GIVEN
        String tagName = "missingTag123456789";

        // WHEN
        tagsRepository.find(tagName)

                // THEN
                .doOnNext(tag -> assertEquals(tag.getTagName(), tagName))
                .doOnNext(tag -> assertEquals(tag.getMovieIds(), Collections.emptySet()))
                .block();
    }


    private List<Tag> generateRandomTags(long number) {
        List<Tag> tags = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            tags.add(generateRandomTag());
        }
        return tags;
    }

    private Tag generateRandomTag() {
        String tagName = generateRandomTagName();
        long id = new Random().nextLong();

        return new Tag(tagName, Set.of(id));
    }

    private String generateRandomTagName() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    private void waitOneSecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
