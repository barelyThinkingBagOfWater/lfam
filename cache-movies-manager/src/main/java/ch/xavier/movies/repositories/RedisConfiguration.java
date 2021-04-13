package ch.xavier.movies.repositories;

import ch.xavier.common.movies.Movie;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.reactive.RedisStringReactiveCommands;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.codec.StringCodec;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import javax.inject.Singleton;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Factory
public class RedisConfiguration {

    @Value("${redis.single.url}")
    private String redisSingleNodeUrl;

    @Value("${redis.cluster.url}")
    private String redisClusterUrl;

    @Value("${redis.nodes.number}")
    private Integer nodesNumber;

    @Value("${manager.repository.timeout.ms}")
    private Integer repositoryTimeout;

    private static final int DEFAULT_PORT = 6379;
    private static final String ID_TEMPLATE = "{id}";


    @Singleton
    public RedisReactiveCommands<String, Movie> factory() {
        if (nodesNumber == 1) {
            log.info("Connecting to a single Redis node with url:{}", redisSingleNodeUrl);

            RedisClient client = RedisClient.create("redis://" + redisSingleNodeUrl + ":" + DEFAULT_PORT);
            client.setDefaultTimeout(Duration.ofMillis(repositoryTimeout));

            return client.connect(new SnappyRedisCodec()).reactive();
        } else {
            log.info("Connecting to a cluster of Redis nodes with {} nodes and template url:{}", nodesNumber, redisSingleNodeUrl);

            List<RedisURI> nodesUri = new ArrayList<>(nodesNumber);

            Flux.range(0, nodesNumber).doOnNext(nodesNumber -> nodesUri.add(
                    new RedisURI(redisClusterUrl.replace(ID_TEMPLATE, String.valueOf(nodesNumber)),
                            DEFAULT_PORT,
                            Duration.ofMillis(repositoryTimeout))))
                    .subscribe();

            RedisClusterClient client = RedisClusterClient.create(nodesUri);
            client.setDefaultTimeout(Duration.ofMillis(repositoryTimeout));
            client.setOptions(ClusterClientOptions.builder()
                    .build());

            return (RedisReactiveCommands) client.connect(new SnappyRedisCodec()).reactive();
        }
    }
}
