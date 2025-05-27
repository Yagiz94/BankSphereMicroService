package com.example.apigateway;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@SpringBootApplication(exclude = RedisRepositoriesAutoConfiguration.class)

public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    ApplicationRunner pingRedis(RedisConnectionFactory f) {
        return args -> System.out.println("→ [REDIS PING] " + f.getConnection().ping());
    }

}
