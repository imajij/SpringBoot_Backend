package com.financetracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.financetracker.repository")
public class MongoConfig {
    // MongoDB configuration is handled via application.yml
    // Auditing is enabled in FinanceTrackerApplication.java
}
