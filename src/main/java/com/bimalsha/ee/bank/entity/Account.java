package com.bimalsha.ee.bank.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "account")
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "account _number", nullable = false, length = 45)
    private String accountNumber;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "account_type_id", nullable = false)
    private AccountType accountType;

    @OneToMany(mappedBy = "fromAccount")
    private List<TransactionHistory> outgoingTransactions;

    @OneToMany(mappedBy = "toAccount")
    private List<TransactionHistory> incomingTransactions;

    // Constructors
    public Account() {
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public List<TransactionHistory> getOutgoingTransactions() {
        return outgoingTransactions;
    }

    public void setOutgoingTransactions(List<TransactionHistory> outgoingTransactions) {
        this.outgoingTransactions = outgoingTransactions;
    }

    public List<TransactionHistory> getIncomingTransactions() {
        return incomingTransactions;
    }

    public void setIncomingTransactions(List<TransactionHistory> incomingTransactions) {
        this.incomingTransactions = incomingTransactions;
    }
}