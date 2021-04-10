package ch.xavier.tags;

import ch.xavier.common.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TagsRepository<T> {
    Mono<T> save(Tag tag);

    Mono<Tag> find(String tagName);

    Flux<Tag> fuzzilyFind(String tagName);

    Mono<Long> count();

    Mono<T> empty();

    Mono<T> addTagToMovie(String tagName, Long movieId);
}
