package com.example.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoCollectionInitializer implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoCollectionInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        String collectionName = "userLogger";
        if (!mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.createCollection(collectionName);
            System.out.println("Created MongoDB collection: " + collectionName);
        } else {
            System.out.println("MongoDB collection already exists: " + collectionName);
        }
    }
}