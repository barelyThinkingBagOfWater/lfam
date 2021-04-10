package ch.xavier.tags;

import ch.xavier.common.tags.messages.TagsAddedMessage;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class TagsMessageReceiver {

    @Autowired
    private TagsCacheManager tagsCacheManager;

    private final Gson gson = new Gson();

    @RabbitListener(queues = "#{rabbitConfig.getQueueName()}")
    public void receiveMessage(Message message) {
        MDC.put("correlationId", message.getMessageProperties().getCorrelationId());

        TagsAddedMessage addTagsMessage = gson.fromJson(new String(message.getBody(), StandardCharsets.UTF_8),
                TagsAddedMessage.class);

        log.info("Received message to add tags:{} to movieIds:{}", addTagsMessage.getTags(), addTagsMessage.getMovieIds());
        tagsCacheManager.addMovieIdsToTags(addTagsMessage.getMovieIds(), addTagsMessage.getTags()).subscribe();

        MDC.remove("correlationId");
    }
}
