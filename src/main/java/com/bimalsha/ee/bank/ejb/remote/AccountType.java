package com.bimalsha.ee.bank.ejb.remote;

/**
 * Interface defining the standard account types available in the banking system.
 */
public interface AccountType {

    // Constants for standard account types
    String CHECKING = "checking";
    String SAVINGS = "savings";
    String FIXED_DEPOSIT = "fixed";
    String CREADIT = "creadit";

    /**
     * Gets the unique identifier for this account.
     * @return the account ID
     */
    String getAccountId();

    /**
     * Gets the type of this account.
     * @return the account type
     */
    String getAccountType();
}