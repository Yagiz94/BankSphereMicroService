package com.example.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@SpringBootApplication(exclude = RedisRepositoriesAutoConfiguration.class)
public class UserApplication {

    private static final Logger logger = LogManager.getLogger(UserApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @Bean
    ApplicationRunner pingRedis(RedisConnectionFactory f) {
        return args -> logger.info("→ [REDIS PING] " + f.getConnection().ping());
    }
}
