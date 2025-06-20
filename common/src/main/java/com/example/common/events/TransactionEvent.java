package com.example.common.events;

import com.example.common.enums.TRANSACTION_TYPE;

import java.io.Serializable;
import java.math.BigDecimal;

public class TransactionEvent implements Serializable {
    private String userName;
    private Long accountId;
    private BigDecimal amount;

    private TRANSACTION_TYPE transactionType;

    public TransactionEvent() {
    }

    // getters/setters
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TRANSACTION_TYPE getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TRANSACTION_TYPE transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public String toString() {
        return "TransactionEvent{" +
                "userName='" + userName + '\'' +
                ", accountId=" + accountId +
                ", amount=" + amount +
                ", transactionType='" + transactionType + '\'' +
                '}';
    }
}
