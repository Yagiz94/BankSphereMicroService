package com.example.transaction.service;

import com.example.common.enums.TRANSACTION_TYPE;
import com.example.common.events.TransactionEvent;
import com.example.transaction.config.KafkaProducerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionPublisher {
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    private static final Logger logger = LogManager.getLogger(TransactionPublisher.class);

    public TransactionPublisher(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String userName, Long accountId,
                        BigDecimal amount, TRANSACTION_TYPE type) {
        TransactionEvent event = new TransactionEvent();
        event.setUserName(userName);
        event.setAccountId(accountId);
        event.setAmount(amount);
        event.setTransactionType(type);
        kafkaTemplate.send(KafkaProducerConfig.TOPIC, event);
        logger.info("🔉 Published to Kafka: " + event);
    }
}
