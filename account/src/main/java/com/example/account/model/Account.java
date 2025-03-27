package com.example.account.model;

import com.example.account.enums.ACCOUNT_TYPE;
import com.example.bankSphere.repository.UserRepository;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accountId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // The owning side of the relationship
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @Enumerated(EnumType.STRING)
    @Column(name = "accountType", nullable = false)
    private ACCOUNT_TYPE accountType;

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    // Default constructor for JPA
    public Account() {}

    // Getters and Setters
    public Long getId() {
        return accountId;
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

    public User getUser() {
        return user;
    }

    // Method to set accountType based on integer value
    public void setAccountType(int accountType) {
        this.accountType = ACCOUNT_TYPE.values()[accountType];  // Set accountType based on enum index
    }

    public void setUser(User user) {
        this.user = user;
    }

    // New method to set the user by user ID
    public void setUserById(Long userId, UserRepository userRepository) {
        this.user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "\"Account\":{" +
                "\n\"accountType\": \"" + accountType + "\", \n\"balance\": " + balance + ", \n\"transactions\":[" +
                showTransactions(transactions) + "\n]" + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(accountId);
    }

    public StringBuilder showTransactions(List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();
        for (Transaction transaction : transactions) {
            sb.append(transaction).append(", ");
        }
        // Remove the trailing comma and space if there are transactions
        if (sb.length() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb;
    }
}
