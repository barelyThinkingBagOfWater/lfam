package ch.xavier.tags;

import ch.xavier.common.tags.messages.TagsAddedMessage;
import ch.xavier.movies.MoviesAndTagsService;
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
    private MoviesAndTagsService moviesAndTagsService;

    private final Gson gson = new Gson();

    //No crash if the Rabbit broker is down as consulting movies is better than nothing, Ops team will be alerted in case of ERRORS
    @RabbitListener(queues = "#{tagsRabbitConfig.getTagsAddedQueueName()}")
    public void receiveTagsAddedMessage(Message message) {
        MDC.put("correlationId", message.getMessageProperties().getCorrelationId());

        TagsAddedMessage tagsAddedMessage = gson.fromJson(new String(message.getBody(), StandardCharsets.UTF_8),
                TagsAddedMessage.class);

        log.info("Received message to add tags:{} to movieIds:{}", tagsAddedMessage.getTags(), tagsAddedMessage.getMovieIds());
        moviesAndTagsService.addTagsToMovies(tagsAddedMessage.getTags(), tagsAddedMessage.getMovieIds()).subscribe();

        MDC.remove("correlationId");
    }
}
