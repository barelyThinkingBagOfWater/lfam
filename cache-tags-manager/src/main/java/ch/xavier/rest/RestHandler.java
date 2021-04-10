package ch.xavier.rest;

import ch.xavier.common.tags.Tag;
import ch.xavier.tags.TagsCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Service
@Slf4j
class RestHandler {

    private final TagsCacheManager tagsCacheManager;

    private final static int SERVICE_NOT_AVAILABLE_HTTP_STATUS_CODE = 503;

    @Autowired
    public RestHandler(TagsCacheManager tagsCacheManager) {
        this.tagsCacheManager = tagsCacheManager;
    }

    Mono<ServerResponse> getTag(ServerRequest request) {
        String tag = request.pathVariable("tag");

        return ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(tagsCacheManager.fuzzilyFind(tag)
                                .switchIfEmpty(Mono.error(new TagNotFoundException()))
                                .doOnComplete(() -> log.info("Tag returned:{}", tag)),
                        Tag.class);
    }

    Mono<ServerResponse> importTagsWithVersion(ServerRequest request) {
        return tagsCacheManager.importAll()
                .then(ok().build());
    }

    Mono<ServerResponse> isCacheReady(ServerRequest serverRequest) {
        return tagsCacheManager.isCacheReady() ?
                ok().build() :
                status(SERVICE_NOT_AVAILABLE_HTTP_STATUS_CODE).build();
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    private static class TagNotFoundException extends RuntimeException {
    }
}