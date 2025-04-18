package com.example.transaction.controller;

import com.example.common.pojo.TransactionEvent;
import com.example.transaction.model.Transaction;
import com.example.transaction.service.TransactionPublisher;
import com.example.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionPublisher transactionPublisher;

    @PostMapping("/process")
    public ResponseEntity<Transaction> processTransaction(
            // Get the userName from the token that is passed by the mutatedExchange in the gateway
            @RequestHeader("userName") String userName,
            @RequestBody TransactionEvent transactionEvent) {

        Transaction transaction = transactionService.processTransaction(userName, transactionEvent.getAccountId(), transactionEvent.getAmount(), transactionEvent.getTransactionType());
        transactionPublisher.publish(userName, transactionEvent.getAccountId(), transactionEvent.getAmount(), transactionEvent.getTransactionType());
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("{accountId}/all")
    public ResponseEntity<?> getAllTransactions(@RequestParam Long accountId) {
        return ResponseEntity.ok(transactionService.getAllTransactions(accountId));
    }
}

