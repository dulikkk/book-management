package kub4k1.bookmanagement.infrastructure.configuration.mongoDb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
class MongoDbConfigurator extends AbstractMongoClientConfiguration {

    @Value("${database.uri}")
    private String databaseURI;

    @Value("${database.name}")
    private String databaseName;

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(databaseURI);
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

}
