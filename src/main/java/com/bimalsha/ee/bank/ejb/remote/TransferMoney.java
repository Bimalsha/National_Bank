package com.bimalsha.ee.bank.ejb.remote;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransferMoney {
    /**
     * Transfers money from one account to another immediately
     *
     * @param fromAccountNumber The source account number
     * @param toAccountNumber The destination account number
     * @param amount The amount to transfer
     * @param note The reason or note for the transfer
     * @return true if the transfer was successful, false otherwise
     */
    boolean transferFunds(String fromAccountNumber, String toAccountNumber, BigDecimal amount, String note);

    /**
     * Schedules a future transfer between accounts
     *
     * @param fromAccountNumber The source account number
     * @param toAccountNumber The destination account number
     * @param amount The amount to transfer
     * @param scheduledDateTime The date and time when the transfer should occur
     * @param note The reason or note for the transfer
     * @return true if the scheduled transfer was created successfully
     */
    boolean scheduleTransfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount,
                             LocalDateTime scheduledDateTime, String note);

    /**
     * Validates if a transfer can be completed
     *
     * @param fromAccountNumber The source account number
     * @param amount The amount to transfer
     * @return true if the account has sufficient funds and is valid for transfers
     */
    boolean validateTransfer(String fromAccountNumber, BigDecimal amount);
}