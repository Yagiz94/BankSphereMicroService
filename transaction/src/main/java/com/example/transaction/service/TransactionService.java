// service/TransactionService.java
package com.example.transaction.service;

import com.example.transaction.model.Transaction;
import com.example.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    /* TODO Rabbit Message query sample that can be used in the project
    @Autowired
    private RabbitTemplate rabbitTemplate;  // Used for event-driven messaging
    */

    @Transactional
    public Transaction withdraw(Transaction transaction) {
        // check balance for a transfer
        Account account = transaction.getAccount();
        if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
            throw new InsufficientFundException("Insufficient balance amount to withdraw from account");
        }

        // Deduct amount and update balance (transactional handling recommended)
        account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        accountRepository.save(account);

        // Update transaction status and save
        transaction.setStatus("SUCCESS");
        transaction = transactionRepository.save(transaction);

        // Publish transaction event to messaging system (e.g., RabbitMQ)
        //TODO rabbitTemplate.convertAndSend("transaction.exchange", "transaction.routing", transaction);

        return transaction;
    }

    @Transactional
    public Transaction deposit(Transaction transaction) {
        // Check if deposit amount is positive
        Account account = transaction.getAccount();
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }

        // Add amount to the account balance and update it
        account.setBalance(account.getBalance().add(transaction.getAmount()));

        // Save updated account
        accountRepository.save(account);

        // Set transaction status to "SUCCESS"
        transaction.setStatus("SUCCESS");

        // Save the transaction
        transaction = transactionRepository.save(transaction);

        // Optionally, publish the transaction event to a messaging system (e.g., RabbitMQ)
        // TODO: rabbitTemplate.convertAndSend("transaction.exchange", "transaction.routing", transaction);

        // Return the updated transaction
        return transaction;
    }

    public List<Transaction> getAllTransactions(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow();
        return account.getTransactions();
    }
}
