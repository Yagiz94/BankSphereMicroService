package com.example.user.repository;

import com.example.bankSphere.entity.UserLogger;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserLoggerRepository extends MongoRepository<UserLogger, String> {
    // No need to add any methods here, Spring Data MongoDB will automatically provide CRUD operations
}