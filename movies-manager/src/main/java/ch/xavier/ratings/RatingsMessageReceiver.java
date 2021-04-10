package ch.xavier.ratings;

import ch.xavier.common.ratings.message.RatingAddedMessage;
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
public class RatingsMessageReceiver {

    @Autowired
    private RatingsService ratingsService;

    private final Gson gson = new Gson();


    @RabbitListener(queues = "#{ratingsRabbitConfig.getRatingAddedQueueName()}")
    public void receiveRatingAddedMessage(Message message) {
        MDC.put("correlationId", message.getMessageProperties().getCorrelationId());

        RatingAddedMessage ratingAddedMessage = gson.fromJson(new String(message.getBody(), StandardCharsets.UTF_8),
                RatingAddedMessage.class);

        log.info("Received message to add rating:{} to movieIds:{}", ratingAddedMessage.getRating(), ratingAddedMessage.getMovieId());
        ratingsService.addRatingToMovie(ratingAddedMessage.getRating(), ratingAddedMessage.getMovieId()).subscribe();

        MDC.remove("correlationId");
    }
}
