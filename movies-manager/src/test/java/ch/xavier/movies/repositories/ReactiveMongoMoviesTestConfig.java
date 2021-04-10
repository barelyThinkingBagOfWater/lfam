package ch.xavier.movies.repositories;

import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;

@Configuration
public class ReactiveMongoMoviesTestConfig {

    @Bean
    public MongoClient mongoClient() {
        String uri = "mongodb://localhost:27017/test";
        return MongoClients.create(uri);
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        ReactiveMongoTemplate template = new ReactiveMongoTemplate(mongoClient(), "test");

        template.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        template.setWriteResultChecking(WriteResultChecking.EXCEPTION);
        template.setReadPreference(ReadPreference.secondaryPreferred());

        return template;
    }
}