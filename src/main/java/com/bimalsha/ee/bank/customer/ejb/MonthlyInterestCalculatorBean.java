package com.bimalsha.ee.bank.customer.ejb;

import com.bimalsha.ee.bank.entity.Account;
import com.bimalsha.ee.bank.entity.AccountType;
import com.bimalsha.ee.bank.entity.TransactionHistory;
import com.bimalsha.ee.bank.interceptor.LoggingInterceptor;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
@Interceptors(LoggingInterceptor.class)
public class MonthlyInterestCalculatorBean {

    private static final Logger logger = Logger.getLogger(MonthlyInterestCalculatorBean.class.getName());

    @PersistenceContext
    private EntityManager em;

    // Run on the first day of each month at 1:00 AM
    @Schedule(dayOfMonth = "1", hour = "1", minute = "0", second = "0", persistent = false)
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void calculateMonthlyInterest() {
        logger.info("Starting monthly interest calculation process");

        try {
            // Get all accounts
            TypedQuery<Account> query = em.createQuery("SELECT a FROM Account a", Account.class);
            List<Account> accounts = query.getResultList();

            int count = 0;
            for (Account account : accounts) {
                applyInterestToAccount(account);
                count++;

                // Commit in batches to avoid memory issues
                if (count % 50 == 0) {
                    em.flush();
                    logger.info("Processed " + count + " accounts");
                }
            }

            logger.info("Completed monthly interest calculation for " + accounts.size() + " accounts");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during monthly interest calculation", e);
            throw e;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void applyInterestToAccount(Account account) {
        // Skip accounts with zero or negative balance
        if (account.getBalance() <= 0) {
            return;
        }

        // Get interest rate based on account type
        double interestRate = getInterestRateForAccountType(account.getAccountType());

        // Calculate interest amount
        double interestAmount = calculateInterest(account.getBalance(), interestRate);

        // If interest amount is significant, apply it
        if (interestAmount >= 0.01) {
            // Update account balance
            double newBalance = account.getBalance() + interestAmount;
            account.setBalance(newBalance);

            // Create transaction record
            TransactionHistory transaction = createInterestTransaction(account, interestAmount);

            // Persist changes
            em.persist(transaction);
            em.merge(account);

            logger.info(String.format("Applied interest of %.2f to account %s (new balance: %.2f)",
                    interestAmount, account.getAccountNumber(), newBalance));
        }
    }

    private double getInterestRateForAccountType(AccountType accountType) {
        // Get account type ID to determine type - assuming there's an ID field
        Integer typeId = accountType.getId();

        // Determine rate based on account type ID
        switch (typeId) {
            case 1: // Assuming ID 1 is for Savings
                return 0.003; // 0.3% monthly (3.6% annually)
            case 2: // Assuming ID 2 is for Checking
                return 0.001; // 0.1% monthly (1.2% annually)
            case 3: // Assuming ID 3 is for Fixed Deposit
                return 0.005; // 0.5% monthly (6% annually)
            default:
                return 0.002; // 0.2% monthly (2.4% annually)
        }
    }

    private double calculateInterest(double balance, double monthlyRate) {
        // Simple interest calculation (balance * rate)
        double interest = balance * monthlyRate;
        // Round to 2 decimal places
        return Math.round(interest * 100) / 100.0;
    }

    private TransactionHistory createInterestTransaction(Account account, double interestAmount) {
        TransactionHistory transaction = new TransactionHistory();

        // Set the receiving account for the interest payment
        transaction.setToAccount(account);

        // No "from" account for interest payments
        transaction.setFromAccount(null);

        // Set the interest amount
        transaction.setAmount(interestAmount);

        // Set transaction details using appropriate fields
        // These field names might need adjustment based on actual entity

        transaction.setReason("Monthly interest payment");
        transaction.setDateTime(java.util.Date.from(LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant()));


        return transaction;
    }
}