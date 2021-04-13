package ch.xavier.tags;

import ch.xavier.common.tags.messages.TagsAddedMessage;
import ch.xavier.movies.MoviesCacheManager;
import ch.xavier.tags.messages.RabbitConfig;
import com.google.gson.Gson;
import io.micronaut.messaging.annotation.MessageHeader;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

@RabbitListener
@Slf4j
public class TagsMessageReceiver {

    @Inject
    private MoviesCacheManager manager;

    private final Gson gson = new Gson();

    @Queue(value = RabbitConfig.QUEUE_NAME)
    public void receiveMessage(byte[] data, @MessageHeader("correlationId") String correlationId) {
        MDC.put("correlationId", correlationId);

        TagsAddedMessage tagsAddedMessage = gson.fromJson(new String(data, StandardCharsets.UTF_8),
                TagsAddedMessage.class);

        log.info("Received message to add tags:{} to movieIds:{}", tagsAddedMessage.getTags(), tagsAddedMessage.getMovieIds());
        manager.addTagsToMovies(tagsAddedMessage.getTags(), tagsAddedMessage.getMovieIds()).subscribe();

        MDC.remove("correlationId");
    }
}
