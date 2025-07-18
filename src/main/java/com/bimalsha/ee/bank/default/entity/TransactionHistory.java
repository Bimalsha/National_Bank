package com.bimalsha.ee.bank.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "transaction_history")
public class TransactionHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "date_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @ManyToOne
    @JoinColumn(name = "account_id_from", nullable = false)
    private com.bimalsha.ee.bank.entity.Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "account_id_to", nullable = false)
    private com.bimalsha.ee.bank.entity.Account toAccount;

    // Constructors
    public TransactionHistory() {
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public com.bimalsha.ee.bank.entity.Account getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(com.bimalsha.ee.bank.entity.Account fromAccount) {
        this.fromAccount = fromAccount;
    }

    public com.bimalsha.ee.bank.entity.Account getToAccount() {
        return toAccount;
    }

    public void setToAccount(com.bimalsha.ee.bank.entity.Account toAccount) {
        this.toAccount = toAccount;
    }
}