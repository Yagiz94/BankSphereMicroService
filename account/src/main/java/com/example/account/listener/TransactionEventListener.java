package com.example.account.listener;

import com.example.account.service.AccountService;
import com.example.common.enums.TRANSACTION_TYPE;
import com.example.common.events.TransactionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionEventListener {

    private final AccountService accountService;
    private static final Logger logger = LogManager.getLogger(TransactionEventListener.class);

    public TransactionEventListener(AccountService accountService) {
        this.accountService = accountService;
    }

    @KafkaListener(topics = "transaction-events", groupId = "account-service")
    public void onTransactionEvent(TransactionEvent event) {
        logger.info("▶️  Received Kafka event: " + event);
        BigDecimal delta = event.getAmount();
        String userName = event.getUserName();
        if ((event.getTransactionType().equals(TRANSACTION_TYPE.WITHDRAWAL))) {
            delta = delta.negate();
        }
        accountService.updateAccount(event.getAccountId(), delta, event.getTransactionType(), userName);
        logger.info("✅ Updated account " + event.getAccountId() + " by " + delta);
    }
}
