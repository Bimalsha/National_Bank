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

    @Column(name = "account_number", nullable = false, length = 45)
    private String accountNumber;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private com.bimalsha.ee.bank.entity.User user;

    @ManyToOne
    @JoinColumn(name = "account_type_id", nullable = false)
    private com.bimalsha.ee.bank.entity.AccountType accountType;

    @OneToMany(mappedBy = "fromAccount")
    private List<com.bimalsha.ee.bank.entity.TransactionHistory> outgoingTransactions;

    @OneToMany(mappedBy = "toAccount")
    private List<com.bimalsha.ee.bank.entity.TransactionHistory> incomingTransactions;

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

    public com.bimalsha.ee.bank.entity.User getUser() {
        return user;
    }

    public void setUser(com.bimalsha.ee.bank.entity.User user) {
        this.user = user;
    }

    public com.bimalsha.ee.bank.entity.AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(com.bimalsha.ee.bank.entity.AccountType accountType) {
        this.accountType = accountType;
    }

    public List<com.bimalsha.ee.bank.entity.TransactionHistory> getOutgoingTransactions() {
        return outgoingTransactions;
    }

    public void setOutgoingTransactions(List<com.bimalsha.ee.bank.entity.TransactionHistory> outgoingTransactions) {
        this.outgoingTransactions = outgoingTransactions;
    }

    public List<com.bimalsha.ee.bank.entity.TransactionHistory> getIncomingTransactions() {
        return incomingTransactions;
    }

    public void setIncomingTransactions(List<com.bimalsha.ee.bank.entity.TransactionHistory> incomingTransactions) {
        this.incomingTransactions = incomingTransactions;
    }
}