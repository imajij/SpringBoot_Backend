package com.financetracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.financetracker.repository")
@EnableMongoAuditing
public class MongoConfig {
    // MongoDB configuration is handled via application.yml
    // This class enables auditing for @CreatedDate and @LastModifiedDate
}
