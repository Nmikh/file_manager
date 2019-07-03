package com.configs;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {
    @Value("${db.host}")
    private String host;

    @Value("${db.port}")
    private int port;

    @Value("${db.name}")
    private String dbName;

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(host, port);
    }

    @Override
    protected String getDatabaseName() {
        return dbName;
    }

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
    }
}
