// repository/AccountRepository.java
package com.example.account.repository;

import com.example.account.model.Account;
import com.example.bankSphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUser(User user);
    Account findByAccountId(Long accountId);
}
