package com.example.account.dto;

import com.example.account.enums.ACCOUNT_TYPE;

import java.math.BigDecimal;

public class AccountDto {

    private Long accountId; // Add userId to associate with the account
    private Long userId;
    private BigDecimal balance;
    private ACCOUNT_TYPE accountType;
    private List<TransactionDto> transactions; // Can still be included if necessary

    // Default constructor for JPA
    public AccountDto() {
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long id) {
        this.accountId = id;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public ACCOUNT_TYPE getAccountType() {
        return accountType;
    }

    public void setAccountType(ACCOUNT_TYPE accountType) {
        this.accountType = accountType;
    }

    public List<TransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDto> transactions) {
        this.transactions = transactions;
    }
}
