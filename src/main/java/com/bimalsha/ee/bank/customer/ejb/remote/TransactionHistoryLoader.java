package com.bimalsha.ee.bank.customer.ejb.remote;

import com.bimalsha.ee.bank.entity.TransactionHistory;
import jakarta.ejb.Remote;
import java.util.List;

@Remote
public interface TransactionHistoryLoader {
    /**
     * Loads all transactions where the user is either sender or receiver
     */
    List<TransactionHistory> loadUserTransactions(Integer userId);

    /**
     * Loads transactions for a specific account
     */
    List<TransactionHistory> loadAccountTransactions(String accountNumber);

    /**
     * Loads only transactions where user sent money
     */
    List<TransactionHistory> loadUserSentTransactions(Integer userId);

    /**
     * Loads only transactions where user received money
     */
    List<TransactionHistory> loadUserReceivedTransactions(Integer userId);
}