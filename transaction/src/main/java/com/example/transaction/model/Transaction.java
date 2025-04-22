package com.example.transaction.model;

import com.example.common.enums.TRANSACTION_TYPE;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionID;

    @Column(nullable = false)
    private String userName; // User who initiated the transaction

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TRANSACTION_TYPE transaction_type;

    @Column(nullable = false)
    private LocalDateTime timestamp;

//    @Column(nullable = false)
//    private String status; // Status: SUCCESS, PENDING, FAILED, etc.

    // Getters and Setters

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getTransactionID() {
        return transactionID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public TRANSACTION_TYPE getType() {
        return transaction_type;
    }

    public void setType(TRANSACTION_TYPE type) {
        this.transaction_type = type;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionID=" + transactionID +
                ", amount=" + amount +
                ", type='" + transaction_type + '\'' +
                ", timestamp=" + timestamp +
                ", userName='" + userName + '\'';
    }
}
