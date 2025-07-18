package com.bimalsha.ee.bank.ejb;

import com.bimalsha.ee.bank.entity.Account;
import com.bimalsha.ee.bank.entity.AccountType;
import com.bimalsha.ee.bank.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class OpenAccountBean {

    private static final Logger logger = Logger.getLogger(OpenAccountBean.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    // Inner class to return both account ID and number
    public static class AccountResult {
        private final Long id;
        private final String accountNumber;

        public AccountResult(Long id, String accountNumber) {
            this.id = id;
            this.accountNumber = accountNumber;
        }

        public Long getId() {
            return id;
        }

        public String getAccountNumber() {
            return accountNumber;
        }
    }

    /**
     * Checks if a user can open an account of a specific type
     * Returns false if the user already has an account of this type
     */
    public boolean canOpenAccount(Long userId, String accountTypeName) {
        // Method implementation remains the same as before
        logger.log(Level.INFO, "Checking if user {0} can open account type {1}",
                new Object[]{userId, accountTypeName});

        try {
            // Get the user by ID
            User user = entityManager.find(User.class, userId.intValue());
            if (user == null) {
                logger.log(Level.WARNING, "User with ID {0} not found", userId);
                return false;
            }

            // Find the account type ID
            TypedQuery<AccountType> typeQuery = entityManager.createQuery(
                    "SELECT at FROM AccountType at WHERE at.type = :type", AccountType.class);
            typeQuery.setParameter("type", accountTypeName);
            AccountType accountType;

            try {
                accountType = typeQuery.getSingleResult();
            } catch (Exception e) {
                logger.log(Level.WARNING, "Account type {0} not found", accountTypeName);
                return false;
            }

            // Check if user already has an account of this type
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(a) FROM Account a WHERE a.user.id = :userId AND a.accountType.id = :accountTypeId",
                    Long.class);
            query.setParameter("userId", user.getId());
            query.setParameter("accountTypeId", accountType.getId());

            Long count = query.getSingleResult();

            if (count > 0) {
                logger.log(Level.INFO, "User {0} already has an account of type {1}",
                        new Object[]{userId, accountTypeName});
                return false;
            }

            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking if user can open account", e);
            return false;
        }
    }

    // Changed return type from Long to AccountResult
    public AccountResult openAccountForExistingCustomer(Long userId, String accountTypeName, BigDecimal initialDeposit) {
        logger.log(Level.INFO, "Opening {0} account for user {1} with initial deposit {2}",
                new Object[]{accountTypeName, userId, initialDeposit});

        try {
            // Get the user by ID
            User user = entityManager.find(User.class, userId.intValue());
            if (user == null) {
                logger.log(Level.SEVERE, "User with ID {0} not found", userId);
                return null;
            }

            // Find the account type
            TypedQuery<AccountType> query = entityManager.createQuery(
                    "SELECT at FROM AccountType at WHERE at.type = :type", AccountType.class);
            query.setParameter("type", accountTypeName);
            AccountType accountType;

            try {
                accountType = query.getSingleResult();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Account type {0} not found", accountTypeName);
                return null;
            }

            // Create new account
            Account account = new Account();
            account.setUser(user);
            account.setAccountType(accountType);
            account.setBalance(initialDeposit.doubleValue());

            // Generate random account number
            String accountNumber = generateAccountNumber();
            account.setAccountNumber(accountNumber);

            // Persist to database
            entityManager.persist(account);
            entityManager.flush(); // Force immediate write and ID generation

            logger.log(Level.INFO, "Account created successfully with ID: {0}, Number: {1}",
                    new Object[]{account.getId(), accountNumber});

            // Return both account ID and number
            return new AccountResult(account.getId().longValue(), accountNumber);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating account", e);
            e.printStackTrace();
            return null;
        }
    }

    private String generateAccountNumber() {
        // Generate 10-digit account number
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // First digit should not be 0
        sb.append(1 + random.nextInt(9));

        // Generate the rest of the digits
        for (int i = 1; i < 10; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }
}