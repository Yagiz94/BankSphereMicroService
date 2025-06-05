package com.example.user.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class JwtRedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final Logger logger = LogManager.getLogger(JwtRedisService.class);

    /**
     * Save a JWT token in Redis with a specified TTL (in minutes).
     */
    public void saveSecretKey(String userName, String secretKey) {
        // Use token as key and username (or any identifier) as value
        redisTemplate.opsForValue().set(userName, secretKey);
        logger.info(userName + " secret key saved to redis database");
    }

    /**
     * Retrieve the secret key for a given username from Redis.
     */
    public String retrieveSecretKey(String userName) {
        // Retrieve the secret key using the username as the key
        return redisTemplate.opsForValue().get(userName);
    }

    /**
     * Remove a JWT token from Redis (e.g., during logout).
     */
    public void removeSecretKey(String userName) {
        redisTemplate.delete(userName);
        logger.info(userName + " secret key removed from redis database");
    }
}
