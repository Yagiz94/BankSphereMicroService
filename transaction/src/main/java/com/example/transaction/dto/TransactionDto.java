package com.example.transaction.dto;

import com.example.transaction.enums.TRANSACTION_TYPE;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDto {

    private BigDecimal amount;
    private TRANSACTION_TYPE type; // e.g., "TRANSFER", "BILL_PAYMENT"
    private LocalDateTime timestamp;
    private AccountDto account;

    // Status: SUCCESS, PENDING, FAILED, etc.
    private String status;

    // Getters and Setters

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setType(int type) {
        this.type = TRANSACTION_TYPE.values()[type];
    }

    public TRANSACTION_TYPE getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAccount(AccountDto accountDto) {
        this.account = accountDto;
    }
}
