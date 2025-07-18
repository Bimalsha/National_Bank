package com.bimalsha.ee.bank.exception;

import java.math.BigDecimal;

public class TransactionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final TransactionErrorCode errorCode;
    private final String fromAccount;
    private final String toAccount;
    private final BigDecimal amount;

    public enum TransactionErrorCode {
        INSUFFICIENT_FUNDS("Insufficient funds in account"),
        INVALID_ACCOUNT("Account does not exist or is invalid"),
        ACCOUNT_LOCKED("Account is locked or suspended"),
        EXCEEDED_DAILY_LIMIT("Transaction exceeds daily transfer limit"),
        PROCESSING_ERROR("Error processing transaction"),
        INVALID_AMOUNT("Invalid transfer amount"),
        SAME_ACCOUNT("Cannot transfer to the same account"),
        SCHEDULED_DATE_PAST("Scheduled date is in the past");

        private final String message;

        TransactionErrorCode(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public TransactionException(TransactionErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.fromAccount = null;
        this.toAccount = null;
        this.amount = null;
    }

    public TransactionException(TransactionErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.fromAccount = null;
        this.toAccount = null;
        this.amount = null;
    }

    public TransactionException(TransactionErrorCode errorCode, String fromAccount,
                                String toAccount, BigDecimal amount) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
    }

    public TransactionException(TransactionErrorCode errorCode, String message,
                                String fromAccount, String toAccount, BigDecimal amount) {
        super(message);
        this.errorCode = errorCode;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
    }

    public TransactionException(TransactionErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.fromAccount = null;
        this.toAccount = null;
        this.amount = null;
    }

    public TransactionErrorCode getErrorCode() {
        return errorCode;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TransactionException [code=").append(errorCode.name()).append(", message=").append(getMessage());

        if (fromAccount != null) {
            sb.append(", fromAccount=").append(maskAccountNumber(fromAccount));
        }

        if (toAccount != null) {
            sb.append(", toAccount=").append(maskAccountNumber(toAccount));
        }

        if (amount != null) {
            sb.append(", amount=").append(amount);
        }

        sb.append("]");
        return sb.toString();
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return "****";
        }
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
}