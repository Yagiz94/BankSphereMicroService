package com.example.transaction.service;

import com.example.common.enums.TRANSACTION_TYPE;
import com.example.transaction.exception.AccountNotFoundException;
import com.example.transaction.exception.InsufficientFundException;
import com.example.transaction.model.Transaction;
import com.example.transaction.repository.TransactionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public Transaction processTransaction(String userName, Long accountId, BigDecimal amount, TRANSACTION_TYPE transactionType) {
        // Validate account
        // Call Account module to validate the account
        HttpServletRequest incoming = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();
        String authHeader = incoming.getHeader("Authorization");
        // Call Account validate endpoint with the same header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<?> response = restTemplate.exchange(
                "http://localhost:8080/api/account/validate/{accountId}",
                HttpMethod.GET,
                request,
                Object.class,
                Map.of("accountId", accountId)
        );
        if (response.getStatusCode().isError() || !(Objects.equals(response.getBody(), true))) {
            throw new AccountNotFoundException("Account not found");
        }


        // Validate input
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (transactionType.equals(TRANSACTION_TYPE.WITHDRAWAL)) {
            // Send request to account module to get the current balance of the target account
            HttpServletRequest incoming2 = ((ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes()).getRequest();
            String authHeader2 = incoming2.getHeader("Authorization");
            // Call Account balance endpoint with the same header
            HttpHeaders headers2 = new HttpHeaders();
            headers2.set("Authorization", authHeader2);
            HttpEntity<?> request2 = new HttpEntity<>(headers2);

            ResponseEntity<BigDecimal> response2 = restTemplate.exchange(
                    "http://localhost:8080/api/account/{accountId}/balance",
                    HttpMethod.GET,
                    request2,
                    BigDecimal.class,
                    Map.of("accountId", accountId)
            );
            BigDecimal balance = response2.getBody();
            // Compare the current balance amount with the transaction's requested withdrawal amount
            if (Objects.requireNonNull(balance).compareTo(amount) < 0) {
                throw new InsufficientFundException("Insufficient funds");
            } else {
                return getTransaction(userName, accountId, amount, transactionType);
            }
        } else {
            return getTransaction(userName, accountId, amount, transactionType);
        }
    }

    private Transaction getTransaction(String userName, Long accountId, BigDecimal amount, TRANSACTION_TYPE transactionType) {
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
