package com.example.account.listener;

import com.example.account.enums.TRANSACTION_TYPE;
import com.example.account.events.TransactionEvent;
import com.example.account.service.AccountService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionEventListener {

    private final AccountService accountService;

    public TransactionEventListener(AccountService accountService) {
        this.accountService = accountService;
    }

    @KafkaListener(topics = "transaction-events", groupId = "account-service")
    public void onTransactionEvent(TransactionEvent event) {
        System.out.println("▶️  Received Kafka event: " + event);
        BigDecimal delta = event.getAmount();
        String userName = event.getUserName();
        if ((event.getTransactionType().equals(TRANSACTION_TYPE.WITHDRAWAL))) {
            delta = delta.negate();
        }
        accountService.updateAccount(event.getAccountId(), delta, event.getTransactionType(), userName);
        System.out.printf("✅ Updated account %d by %s%n",
                event.getAccountId(), delta);
    }
}
