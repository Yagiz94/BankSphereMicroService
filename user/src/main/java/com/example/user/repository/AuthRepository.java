// repository/UserRepository.java
package com.example.user.repository;

import com.example.bankSphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<User, String> {
    User findByUsername(String username);

    Optional<User> findById(Long id);
}
