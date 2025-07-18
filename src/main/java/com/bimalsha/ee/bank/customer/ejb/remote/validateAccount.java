package com.bimalsha.ee.bank.customer.ejb.remote;

import com.bimalsha.ee.bank.entity.Account;

public interface validateAccount {
    /**
     * Validates if an account number exists in the database
     * @param accountNumber The account number to validate
     * @return The Account object if found, null otherwise
     */
    Account validateAccountNumber(String accountNumber);
}