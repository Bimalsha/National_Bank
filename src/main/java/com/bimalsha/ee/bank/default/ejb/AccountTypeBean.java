package com.bimalsha.ee.bank.ejb;

import com.bimalsha.ee.bank.ejb.remote.AccountType;
import jakarta.ejb.Stateless;

/**
 * EJB implementation of the AccountType interface.
 * Provides account type information for the banking system.
 */
@Stateless
public class AccountTypeBean implements AccountType {

    private String accountId;
    private String accountType;

    // Default constructor
    public AccountTypeBean() {
    }

    // Constructor with parameters
    public AccountTypeBean(String accountId, String accountType) {
        this.accountId = accountId;
        this.accountType = accountType;
    }

    @Override
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    // Utility method to check if the account type is valid
    public boolean isValidAccountType() {
        return accountType != null && (
                accountType.equals(CHECKING) ||
                        accountType.equals(SAVINGS) ||
                        accountType.equals(FIXED_DEPOSIT) ||
                        accountType.equals(CREADIT)
        );
    }
}