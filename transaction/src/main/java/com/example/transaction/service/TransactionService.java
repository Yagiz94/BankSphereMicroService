// service/TransactionService.java
package com.example.transaction.service;

import com.example.common.enums.TRANSACTION_TYPE;
import com.example.transaction.model.Transaction;
import com.example.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public Transaction processTransaction(String userName, Long accountId, BigDecimal amount, TRANSACTION_TYPE transactionType) {
        // Validate input
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        Transaction transaction = new Transaction();
        transaction.setUserName(userName);
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setType(transactionType);
        transaction.setTimestamp(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
}
