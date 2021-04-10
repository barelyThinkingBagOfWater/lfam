package ch.xavier.tags.repositories;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticRestClient {

    public static final String DEFAULT_INDEX_NAME = "tags";
    private static final int ELASTICSEARCH_DEFAULT_PORT = 9200;

    @Bean
    public RestHighLevelClient restHighLevelClient(
            @Value("${elastic.host}") String host,
            @Value("${elastic.timeout.global}") Integer globalTimeoutInMs) {

        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(host, ELASTICSEARCH_DEFAULT_PORT, "http"))
                        .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                                .setConnectTimeout(globalTimeoutInMs)
                                .setConnectionRequestTimeout(globalTimeoutInMs)));
    }
}
