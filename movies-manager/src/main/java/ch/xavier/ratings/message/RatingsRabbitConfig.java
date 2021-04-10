package ch.xavier.ratings.message;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class RatingsRabbitConfig {

    //Adding the hostname is also too random. Use a kind of instanceId passed from a higher level (configMap?) that you can also add to the MDC and in other places (metrics config?)
    private static final String RATING_ADDED_QUEUE_NAME = "rating.added.queue.movie-manager." + UUID.randomUUID();

    @Bean
    Queue ratingsAddedQueue() {
        return new Queue(RATING_ADDED_QUEUE_NAME, false);
    }

    @Bean
    FanoutExchange ratingAddedExchange() {
        return new FanoutExchange("rating.added.exchange", true, false);
    }

    @Bean
    Binding ratingAddedBinding() {
        return BindingBuilder.bind(ratingsAddedQueue()).to(ratingAddedExchange());
    }

    public String getRatingAddedQueueName() {
        return RATING_ADDED_QUEUE_NAME;
    }
}
