package ch.xavier.tags.messages;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.UUID;

@Configuration
public class RabbitConfig {

    //Adding the hostname is also too random. Use a kind of instanceId passed from a higher level (configMap?) that you can also add to the MDC and in other places (metrics config?)
    private static final String QUEUE_NAME = "tags.added.queue.cache-tags-manager." + UUID.randomUUID();

    @Bean
    Queue tagsAddedQueue() throws IOException {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    FanoutExchange tagsAddedExchange() {
        return new FanoutExchange("tags.added.exchange", true, false);
    }

    @Bean
    Binding tagsAddedBinding() throws IOException {
        return BindingBuilder.bind(tagsAddedQueue()).to(tagsAddedExchange());
    }

    public String getQueueName() {
        return QUEUE_NAME;
    }
}
