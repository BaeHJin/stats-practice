package core.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages="core.repository", considerNestedRepositories = true)
public class MongoConfig {

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Bean
    public MongoClient mongoClient() {
        MongoClientOptions.Builder optionsBuilder = new MongoClientOptions.Builder();
        optionsBuilder.description(database);
        optionsBuilder.applicationName(database);
        optionsBuilder.retryWrites(true);
        optionsBuilder.minConnectionsPerHost(10);
        optionsBuilder.maxWaitTime(1_500);
        optionsBuilder.maxConnectionLifeTime(60 * 60_000);
        optionsBuilder.connectTimeout(1_000);
        optionsBuilder.socketTimeout(1_500);
        //optionsBuilder.serverSelectionTimeout(0); 디폴트 설정에 따른다.
        optionsBuilder.writeConcern(WriteConcern.ACKNOWLEDGED.withW(1));

        return new MongoClient(new MongoClientURI(mongoUri, optionsBuilder));
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), database);
    }
}
