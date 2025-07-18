package com.bimalsha.ee.bank.customer.ejb;

import com.bimalsha.ee.bank.customer.ejb.remote.TransferMoney;
import com.bimalsha.ee.bank.entity.Account;
import com.bimalsha.ee.bank.entity.ScheduledTransfer;
import com.bimalsha.ee.bank.entity.TransactionHistory;
import com.bimalsha.ee.bank.exception.TransactionException;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Logger;

@Stateless
public class TransferMoneyBean implements TransferMoney {

    private static final Logger logger = Logger.getLogger(TransferMoneyBean.class.getName());

    @PersistenceContext(unitName = "NationalB")
    private EntityManager entityManager;

    @Resource
    private TimerService timerService;

    @Override
    public boolean transferFunds(String fromAccountNumber, String toAccountNumber, BigDecimal amount, String note) {
        try {
            // Validate transfer
            if (!validateTransfer(fromAccountNumber, amount)) {
                throw new TransactionException(
                        TransactionException.TransactionErrorCode.INSUFFICIENT_FUNDS,
                        fromAccountNumber, toAccountNumber, amount);
            }

            // Find accounts
            Account fromAccount = findAccount(fromAccountNumber);
            Account toAccount = findAccount(toAccountNumber);

            if (fromAccount == null) {
                throw new TransactionException(
                        TransactionException.TransactionErrorCode.INVALID_ACCOUNT,
                        "Source account not found", fromAccountNumber, toAccountNumber, amount);
            }

            if (toAccount == null) {
                throw new TransactionException(
                        TransactionException.TransactionErrorCode.INVALID_ACCOUNT,
                        "Destination account not found", fromAccountNumber, toAccountNumber, amount);
            }

            // Execute transfer
            executeTransfer(fromAccount, toAccount, amount, note);
            return true;
        } catch (TransactionException te) {
            logger.severe("Transaction error: " + te.getMessage());
            throw te;
        } catch (Exception e) {
            logger.severe("Unexpected error: " + e.getMessage());
            throw new TransactionException(
                    TransactionException.TransactionErrorCode.PROCESSING_ERROR, e);
        }
    }

    @Override
    public boolean scheduleTransfer(String fromAccountNumber, String toAccountNumber,
                                    BigDecimal amount, LocalDateTime scheduledDateTime, String note) {
        try {
            // Validate accounts exist
            Account fromAccount = findAccount(fromAccountNumber);
            Account toAccount = findAccount(toAccountNumber);

            if (fromAccount == null) {
                throw new TransactionException(
                        TransactionException.TransactionErrorCode.INVALID_ACCOUNT,
                        "Source account not found", fromAccountNumber, toAccountNumber, amount);
            }

            if (toAccount == null) {
                throw new TransactionException(
                        TransactionException.TransactionErrorCode.INVALID_ACCOUNT,
                        "Destination account not found", fromAccountNumber, toAccountNumber, amount);
            }

            // Validate scheduled date is in the future
            if (scheduledDateTime.isBefore(LocalDateTime.now())) {
                throw new TransactionException(
                        TransactionException.TransactionErrorCode.SCHEDULED_DATE_PAST,
                        fromAccountNumber, toAccountNumber, amount);
            }

            // Create serializable timer info
            ScheduledTransferInfo timerInfo = new ScheduledTransferInfo(
                    fromAccountNumber, toAccountNumber, amount.doubleValue(), note);

            // Convert LocalDateTime to Date for the timer
            Date scheduledDate = Date.from(scheduledDateTime
                    .atZone(ZoneId.systemDefault())
                    .toInstant());

            // Create a timer for the scheduled transfer
            Timer timer = timerService.createTimer(scheduledDate, timerInfo);

            // Save the scheduled transfer in database with the timer ID
            Long transferId = saveScheduledTransfer(fromAccount, toAccount, amount,
                    scheduledDateTime, note, timer.toString());

            logger.info("Scheduled transfer created: ID=" + transferId +
                    ", Date=" + scheduledDateTime +
                    ", Amount=" + amount);



            return true;
        } catch (TransactionException te) {
            logger.severe("Transaction error: " + te.getMessage());
            throw te;
        } catch (Exception e) {
            logger.severe("Error scheduling transfer: " + e.getMessage());
            throw new TransactionException(
                    TransactionException.TransactionErrorCode.PROCESSING_ERROR, e);
        }
    }

    @Override
    public boolean validateTransfer(String fromAccountNumber, BigDecimal amount) {
        Account fromAccount = findAccount(fromAccountNumber);

        // Check if account exists and has sufficient funds
        if (fromAccount == null) {
            return false;
        }

        return fromAccount.getBalance() >= amount.doubleValue();
    }

    @Timeout
    public void executeScheduledTransfer(Timer timer) {
        ScheduledTransferInfo info = (ScheduledTransferInfo) timer.getInfo();
        logger.info("Executing scheduled transfer: " + info.getFromAccountNumber() +
                " to " + info.getToAccountNumber() +
                ", Amount: " + info.getAmount());

        try {
            String fromAccountNumber = info.getFromAccountNumber();
            String toAccountNumber = info.getToAccountNumber();
            BigDecimal amount = BigDecimal.valueOf(info.getAmount());
            String note = info.getNote();

            // Find the scheduled transfer record
            ScheduledTransfer scheduledTransfer = findScheduledTransferByTimerId(timer.toString());

            if (scheduledTransfer == null) {
                logger.warning("No scheduled transfer record found for timer: " + timer);
                // Still try to execute the transfer based on timer info
            }

            // Validate accounts and balance before executing
            Account fromAccount = findAccount(fromAccountNumber);
            Account toAccount = findAccount(toAccountNumber);

            if (fromAccount == null || toAccount == null) {
                if (scheduledTransfer != null) {
                    scheduledTransfer.setStatus("FAILED");
                    scheduledTransfer.setStatusReason("Account not found");
                    entityManager.merge(scheduledTransfer);
                }
                logger.severe("Account not found for scheduled transfer");
                return;
            }

            // Check sufficient funds
            if (fromAccount.getBalance() < amount.doubleValue()) {
                if (scheduledTransfer != null) {
                    scheduledTransfer.setStatus("FAILED");
                    scheduledTransfer.setStatusReason("Insufficient funds");
                    entityManager.merge(scheduledTransfer);
                }
                logger.severe("Insufficient funds for scheduled transfer");
                return;
            }

            // Execute the transfer
            executeTransfer(fromAccount, toAccount, amount, note);

            // Update scheduled transfer status
            if (scheduledTransfer != null) {
                updateScheduledTransferStatus(scheduledTransfer);
            }

            logger.info("Scheduled transfer completed successfully");
        } catch (Exception e) {
            logger.severe("Error executing scheduled transfer: " + e.getMessage());
            // Update status if possible
            try {
                ScheduledTransfer scheduledTransfer = findScheduledTransferByTimerId(timer.toString());
                if (scheduledTransfer != null) {
                    scheduledTransfer.setStatus("FAILED");
                    scheduledTransfer.setStatusReason(e.getMessage());
                    entityManager.merge(scheduledTransfer);
                }
            } catch (Exception ex) {
                logger.severe("Error updating scheduled transfer status: " + ex.getMessage());
            }
        }
    }

    private Account findAccount(String accountNumber) {
        TypedQuery<Account> query = entityManager.createQuery(
                "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber",
                Account.class);
        query.setParameter("accountNumber", accountNumber);

        return query.getResultList().stream().findFirst().orElse(null);
    }

    private void executeTransfer(Account fromAccount, Account toAccount, BigDecimal amount, String note) {
        // Update account balances
        fromAccount.setBalance(fromAccount.getBalance() - amount.doubleValue());
        toAccount.setBalance(toAccount.getBalance() + amount.doubleValue());

        entityManager.merge(fromAccount);
        entityManager.merge(toAccount);

        // Record transaction history
        TransactionHistory transaction = new TransactionHistory();
        transaction.setAmount(amount.doubleValue());
        transaction.setDateTime(new Date());
        transaction.setReason(note != null ? note : "Transfer");
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);

        entityManager.persist(transaction);
    }

    private Long saveScheduledTransfer(Account fromAccount, Account toAccount,
                                       BigDecimal amount, LocalDateTime scheduledDateTime,
                                       String note, String timerId) {
        ScheduledTransfer scheduledTransfer = new ScheduledTransfer();
        scheduledTransfer.setFromAccount(fromAccount);
        scheduledTransfer.setToAccount(toAccount);
        scheduledTransfer.setAmount(amount.doubleValue());
        scheduledTransfer.setScheduledDate(Date.from(scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        scheduledTransfer.setNote(note);
        scheduledTransfer.setStatus("PENDING");
        scheduledTransfer.setTimerId(timerId);
        scheduledTransfer.setCreatedDate(new Date());

        entityManager.persist(scheduledTransfer);
        return scheduledTransfer.getId();
    }

    private void updateScheduledTransferStatus(ScheduledTransfer scheduledTransfer) {
        scheduledTransfer.setStatus("COMPLETED");
        scheduledTransfer.setExecutedDate(new Date());
        entityManager.merge(scheduledTransfer);
    }

    private ScheduledTransfer findScheduledTransferByTimerId(String timerId) {
        TypedQuery<ScheduledTransfer> query = entityManager.createQuery(
                "SELECT s FROM ScheduledTransfer s WHERE s.timerId = :timerId",
                ScheduledTransfer.class);
        query.setParameter("timerId", timerId);
        return query.getResultList().stream().findFirst().orElse(null);
    }

    // Serializable class for timer info
    public static class ScheduledTransferInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        private String fromAccountNumber;
        private String toAccountNumber;
        private double amount;
        private String note;

        public ScheduledTransferInfo(String fromAccountNumber, String toAccountNumber,
                                     double amount, String note) {
            this.fromAccountNumber = fromAccountNumber;
            this.toAccountNumber = toAccountNumber;
            this.amount = amount;
            this.note = note;
        }

        public String getFromAccountNumber() {
            return fromAccountNumber;
        }

        public String getToAccountNumber() {
            return toAccountNumber;
        }

        public double getAmount() {
            return amount;
        }

        public String getNote() {
            return note;
        }
    }
}