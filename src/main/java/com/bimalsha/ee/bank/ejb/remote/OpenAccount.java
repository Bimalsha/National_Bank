package com.bimalsha.ee.bank.ejb.remote;

import java.math.BigDecimal;
import java.util.List;

/**
 * Remote interface for account opening and management operations.
 */
public interface OpenAccount {

    boolean canOpenAccount(Long userId, String accountType);
    Long openAccountForExistingCustomer(Long userId, String accountType, BigDecimal initialDeposit);
}