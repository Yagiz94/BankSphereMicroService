package com.example.apigateway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@SpringBootApplication(exclude = RedisRepositoriesAutoConfiguration.class)
public class ApiGatewayApplication {

    // Define a logger instance
    private static final Logger logger = LogManager.getLogger(ApiGatewayApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    ApplicationRunner pingRedis(RedisConnectionFactory f) {
        return args -> {
            String pingResult = f.getConnection().ping();
            // Use the logger instead of System.out.println()
            logger.info("→ [REDIS PING] " + pingResult);
        };
    }
}