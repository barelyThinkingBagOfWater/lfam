package ch.xavier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;

@SpringBootApplication(exclude = ElasticsearchDataAutoConfiguration.class)
public class TagsCacheExplorerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TagsCacheExplorerApplication.class, args);
    }
}