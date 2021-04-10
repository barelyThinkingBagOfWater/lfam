package ch.xavier.movies.repositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import reactor.core.publisher.Flux;

import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class ReactiveMongoMoviesConfig {

    @Value("${application.name}")
    private String applicationName;

    @Value("${mongodb.database}")
    private String databaseName;

    @Value("${mongodb.host}")
    private String host;

    @Value("${mongodb.nodes.number}")
    private String nodesNumber;

    @Value("${mongodb.username}")
    private String username;

    @Value("${mongodb.password}")
    private String password;

    @Value("${mongodb.replicaSet}")
    private String replicaSetName;

    @Value("${mongodb.minPoolSize}")
    private Integer minPoolSize;

    @Value("${mongodb.maxPoolSize}")
    private Integer maxPoolSize;

    private static final int MONGODB_DEFAULT_PORT = 27017;
    private static final String ID_PLACEHOLDER = "{id}";
    private static final int SERVER_SELECTION_TIMEOUT_DEFAULT_SECONDS = 10;

    //to close this app if mongo is unreachable
    @Autowired
    private ConfigurableApplicationContext ctx;


    @Bean
    public MongoClient mongoClient() {
        String connectionUrl = "";

        if (isClusterConnection()) {
            connectionUrl = "mongodb://" + username + ":" + password + "@" + createClusterConnectionUrl() + ":"
                    + MONGODB_DEFAULT_PORT + "/" + databaseName + "?replicaSet=".concat(replicaSetName);
        } else {
            connectionUrl = "mongodb://" + username + ":" + password + "@" + host + ":" + MONGODB_DEFAULT_PORT
                    + "/" + databaseName;
        }

        MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder()
                .applicationName(applicationName)
                .applyConnectionString(new ConnectionString(connectionUrl))
                .applyToConnectionPoolSettings(connectionPoolBuilder -> connectionPoolBuilder
                        .minSize(minPoolSize)
                        .maxSize(maxPoolSize)
                        .build())
                .applyToClusterSettings(builder ->
                        builder.serverSelectionTimeout(SERVER_SELECTION_TIMEOUT_DEFAULT_SECONDS, TimeUnit.SECONDS)
                );

        if (!isClusterConnection()) {
            log.info("No cluster configuration found, connecting to the single node Mongodb instance at :{}", host);
//            addSSLSettingsAndSetSSLEnvVariables(settingsBuilder);
        } else {
            log.info("Cluster configuration found for {} nodes, using connectionUrl:{}", nodesNumber, connectionUrl);
        }

        MongoClient mongoClient = MongoClients.create(settingsBuilder.build());

        //Hack to detect when MongoDb is down
        try {
            new ReactiveMongoTemplate(mongoClient, databaseName).executeCommand("{ ping: 1 }").block();
        } catch (DataAccessResourceFailureException e) {
            log.error("Timeout received when connecting to MongoDb, now closing this application", e);
            SpringApplication.exit(ctx, () -> 0);
        }

        return mongoClient;
    }

    private void addSSLSettingsAndSetSSLEnvVariables(MongoClientSettings.Builder settingsBuilder) {
        settingsBuilder.applyToSslSettings(sslSettingsBuilder -> sslSettingsBuilder
                .enabled(true)
                .invalidHostNameAllowed(true)
                .build());

        System.setProperty("javax.net.ssl.trustStore", "/certs/db_client.ts");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");

        System.setProperty("javax.net.ssl.keyStore", "/certs/db_client.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        ReactiveMongoTemplate template = new ReactiveMongoTemplate(mongoClient(), databaseName);

        template.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        template.setWriteResultChecking(WriteResultChecking.EXCEPTION);
        template.setReadPreference(ReadPreference.secondaryPreferred());
        return template;
    }

    private boolean isClusterConnection() {
        return !replicaSetName.isEmpty() && Integer.parseInt(nodesNumber) > 1;
    }

    private String createClusterConnectionUrl() {
        StringJoiner sj = new StringJoiner(",");

        Flux.range(0, Integer.parseInt(nodesNumber))
                .doOnNext(nodesNumber -> sj.add(host.replace(ID_PLACEHOLDER, String.valueOf(nodesNumber))))
                .subscribe();

        return sj.toString();
    }
}