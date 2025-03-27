// controller/AccountController.java
package com.example.account.controller;

import com.example.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @GetMapping("/user/{userId}/account/{accountId}/transactions")
    public ResponseEntity<?> getAccountTransactions(@PathVariable Long userId, @PathVariable Long accountId) {
        User user = userService.retrieveUser(userId);
        if (user == null) {
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        }
        Account account = accountService.retrieveAccount(accountId);
        if (account == null) {
            return new ResponseEntity<>("Account not found.", HttpStatus.NOT_FOUND);
        }

        if (account.getUser().getId().equals(userId)) {
            try {
                List<TransactionDto> transactions = accountService.getAllTransactions(userId, accountId);
                // Retrieve all transactions associated with the account that belongs to the user
                if (transactions.isEmpty()) {
                    return ResponseEntity.ok(Map.of("Message", "No transactions found for the account."));
                } else {
                    return ResponseEntity.ok(transactions);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                return new ResponseEntity<>("Error processing the deposit.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Transaction operation failed", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/user/{userId}/account/{accountId}/transaction/deposit")
    public ResponseEntity<?> deposit(@RequestBody TransactionDto transactionDto, @PathVariable Long
            userId, @PathVariable Long accountId) {

        // Validate and retrieve account objects for processing transaction
        User user = userService.retrieveUser(userId);
        if (user == null) {
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        }
        Account account = accountService.retrieveAccount(accountId);
        if (account == null) {
            return new ResponseEntity<>("Account not found.", HttpStatus.NOT_FOUND);
        }

        // Check if the user & account exist and account belongs to the user
        if (account.getUser().getId().equals(userId)) {
            // Simple checks
            if (transactionDto.getAmount() == null || transactionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return new ResponseEntity<>("Amount must be greater than zero.", HttpStatus.BAD_REQUEST);
            }
            if (transactionDto.getStatus() == null || transactionDto.getStatus().isEmpty()) {
                return new ResponseEntity<>("Status is required.", HttpStatus.BAD_REQUEST);
            }

            // Create transaction and set account
            Transaction transaction = new Transaction();
            transaction.setAmount(transactionDto.getAmount());
            transaction.setType(transactionDto.getType().getValue());
            transaction.setTimestamp(LocalDateTime.now());  // Ensure proper conversion
            transaction.setStatus(transactionDto.getStatus());

            // IMPORTANT: Set the account on the transaction
            transaction.setAccount(account);  // Associate the account with the transaction

            try {
                // Perform the deposit
                accountService.deposit(transaction);

                // Return the updated account
                return ResponseEntity.ok("Deposit operation is successful");
            } catch (Exception e) {
                // Log the exception (use a logger for production)
                e.printStackTrace();
                return new ResponseEntity<>("Error processing the deposit.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Transaction operation failed", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/user/{userId}/account/{accountId}/transaction/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody TransactionDto transactionDto, @PathVariable Long
            userId, @PathVariable Long accountId) {

        User user = userService.retrieveUser(userId);
        if (user == null) {
            return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
        }
        Account account = accountService.retrieveAccount(accountId);
        if (account == null) {
            return new ResponseEntity<>("Account not found.", HttpStatus.NOT_FOUND);
        }

        // Check if the user & account exist and account belongs to the user
        if (account.getUser().getId().equals(userId)) {
            // Simple validation checks for the withdrawal request
            if (transactionDto.getAmount() == null || transactionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return new ResponseEntity<>("Amount must be greater than zero.", HttpStatus.BAD_REQUEST);
            }
            if (transactionDto.getStatus() == null || transactionDto.getStatus().isEmpty()) {
                return new ResponseEntity<>("Status is required.", HttpStatus.BAD_REQUEST);
            }

            // Ensure the account has enough balance for the withdrawal
            if (account.getBalance().compareTo(transactionDto.getAmount()) < 0) {
                return new ResponseEntity<>("Insufficient funds.", HttpStatus.BAD_REQUEST);
            }

            // Create the transaction and set necessary fields
            Transaction transaction = new Transaction();
            transaction.setAmount(transactionDto.getAmount());
            transaction.setType(transactionDto.getType().getValue());
            transaction.setTimestamp(LocalDateTime.now()); // Ensure correct parsing
            transaction.setStatus(transactionDto.getStatus());

            // IMPORTANT: Set the account for the transaction to ensure account_id is populated
            transaction.setAccount(account);  // Associate the transaction with the account

            try {
                // Process the withdrawal by calling the service method
                accountService.withdraw(transaction);

                // Return the updated account information after withdrawal
                return ResponseEntity.ok("Withdraw operation is successful");
            } catch (Exception e) {
                // Log the exception (use a logger for production)
                e.printStackTrace();
                return new ResponseEntity<>("Error processing the withdrawal.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Transaction operation failed", HttpStatus.NOT_FOUND);
        }
    }
}
