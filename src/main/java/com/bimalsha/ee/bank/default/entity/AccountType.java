package com.bimalsha.ee.bank.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "account_type")
public class AccountType implements Serializable {

    @Id
    private Integer id;

    @Column(name = "type", nullable = false, length = 45)
    private String type;

    @OneToMany(mappedBy = "accountType")
    private List<Account> accounts;

    // Constructors
    public AccountType() {
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}