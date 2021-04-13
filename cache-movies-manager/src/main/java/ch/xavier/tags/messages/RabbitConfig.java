package ch.xavier.tags.messages;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import io.micronaut.rabbitmq.connect.ChannelInitializer;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.UUID;

@Singleton
public class RabbitConfig extends ChannelInitializer {

    //Adding the hostname is also too random. Use a kind of instanceId/correlationId passed from a higher level (configMap?) that you can also add to the MDC and in other places (metrics config?)
//    public static final String QUEUE_NAME = "tags.added.queue.cache-movies-manager." + UUID.randomUUID();

    //Micronaut doesn't support EL in annotations
    public static final String QUEUE_NAME = "tags.added.queue.cache-movies-manager";
    private static final String EXCHANGE_NAME = "tags.added.exchange";

    @Override
    public void initialize(Channel channel) throws IOException {
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT, true);

        channel.queueDeclare(QUEUE_NAME, false, false, true, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, QUEUE_NAME);
    }
}
