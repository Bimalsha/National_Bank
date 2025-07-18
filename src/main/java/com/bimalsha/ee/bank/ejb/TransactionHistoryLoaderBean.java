package com.bimalsha.ee.bank.ejb;

import com.bimalsha.ee.bank.ejb.remote.TransactionHistoryLoader;
import com.bimalsha.ee.bank.entity.TransactionHistory;
import com.bimalsha.ee.bank.interceptor.LoggingInterceptor;

import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Stateless
@Interceptors(LoggingInterceptor.class)
public class TransactionHistoryLoaderBean implements TransactionHistoryLoader {

    private static final Logger logger = Logger.getLogger(TransactionHistoryLoaderBean.class.getName());

    @PersistenceContext(unitName = "NationalB")
    private EntityManager em;

    @Override
    public List<TransactionHistory> loadUserTransactions(Integer userId) {
        logger.info("Loading all transactions for user ID: " + userId);

        // Get user's account numbers
        TypedQuery<String> accountQuery = em.createQuery(
                "SELECT a.accountNumber FROM Account a WHERE a.user.id = :userId",
                String.class);
        accountQuery.setParameter("userId", userId);
        List<String> userAccounts = accountQuery.getResultList();

        if (userAccounts.isEmpty()) {
            logger.info("No accounts found for user ID: " + userId);
            return new ArrayList<>();
        }

        // Get transactions for these accounts
        TypedQuery<TransactionHistory> txQuery = em.createQuery(
                "SELECT t FROM TransactionHistory t WHERE " +
                        "(t.fromAccount.accountNumber IN :accountNumbers OR " +
                        "t.toAccount.accountNumber IN :accountNumbers) " +
                        "ORDER BY t.dateTime DESC",
                TransactionHistory.class);
        txQuery.setParameter("accountNumbers", userAccounts);

        List<TransactionHistory> transactions = txQuery.getResultList();
        logger.info("Found " + transactions.size() + " transactions for user ID: " + userId);

        return transactions;
    }

    @Override
    public List<TransactionHistory> loadAccountTransactions(String accountNumber) {
        logger.info("Loading transactions for account: " + accountNumber);

        TypedQuery<TransactionHistory> query = em.createQuery(
                "SELECT t FROM TransactionHistory t WHERE " +
                        "t.fromAccount.accountNumber = :accountNumber OR " +
                        "t.toAccount.accountNumber = :accountNumber " +
                        "ORDER BY t.dateTime DESC",
                TransactionHistory.class);
        query.setParameter("accountNumber", accountNumber);

        return query.getResultList();
    }

    @Override
    public List<TransactionHistory> loadUserSentTransactions(Integer userId) {
        logger.info("Loading sent transactions for user ID: " + userId);

        // Get user account numbers
        TypedQuery<String> accountQuery = em.createQuery(
                "SELECT a.accountNumber FROM Account a WHERE a.user.id = :userId",
                String.class);
        accountQuery.setParameter("userId", userId);
        List<String> userAccounts = accountQuery.getResultList();

        if (userAccounts.isEmpty()) {
            return new ArrayList<>();
        }

        // Get transactions where user accounts are the source
        TypedQuery<TransactionHistory> txQuery = em.createQuery(
                "SELECT t FROM TransactionHistory t WHERE " +
                        "t.fromAccount.accountNumber IN :accountNumbers " +
                        "ORDER BY t.dateTime DESC",
                TransactionHistory.class);
        txQuery.setParameter("accountNumbers", userAccounts);

        return txQuery.getResultList();
    }

    @Override
    public List<TransactionHistory> loadUserReceivedTransactions(Integer userId) {
        logger.info("Loading received transactions for user ID: " + userId);

        // Get user account numbers
        TypedQuery<String> accountQuery = em.createQuery(
                "SELECT a.accountNumber FROM Account a WHERE a.user.id = :userId",
                String.class);
        accountQuery.setParameter("userId", userId);
        List<String> userAccounts = accountQuery.getResultList();

        if (userAccounts.isEmpty()) {
            return new ArrayList<>();
        }

        // Get transactions where user accounts are the destination
        TypedQuery<TransactionHistory> txQuery = em.createQuery(
                "SELECT t FROM TransactionHistory t WHERE " +
                        "t.toAccount.accountNumber IN :accountNumbers " +
                        "ORDER BY t.dateTime DESC",
                TransactionHistory.class);
        txQuery.setParameter("accountNumbers", userAccounts);

        return txQuery.getResultList();
    }
}