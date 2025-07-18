package com.bimalsha.ee.bank.customer.servlet;

import com.bimalsha.ee.bank.customer.ejb.remote.TransferMoney;
import com.bimalsha.ee.bank.customer.ejb.remote.UserSessionLoader;
import com.bimalsha.ee.bank.entity.Account;
import com.bimalsha.ee.bank.exception.TransactionException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet("/transferMoney")
public class TransferMoneyServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(TransferMoneyServlet.class.getName());

    @EJB
    private TransferMoney transferMoneyBean;

    @EJB
    private UserSessionLoader userSessionLoader;  // Changed from UserSessionLoaderBean to UserSessionLoader

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = determineAction(request);
        Map<String, String> errors = new HashMap<>();
        boolean success = false;
        String message = "";

        try {
            if ("standard".equals(action)) {
                success = processStandardTransfer(request, errors);
                message = "Transfer completed successfully";
            } else if ("scheduled".equals(action)) {
                success = processScheduledTransfer(request, errors);
                message = "Transfer scheduled successfully";
            } else {
                errors.put("general", "Invalid form submission");
            }

            if (success) {
                // Get the user ID from the session
                HttpSession session = request.getSession();
                Integer userId = (Integer) session.getAttribute("userId");

                if (userId != null) {
                    // Reload user accounts with updated balances
                    List<Account> updatedAccounts = userSessionLoader.loadUserAccounts(userId);

                    // Update session with fresh account data
                    session.setAttribute("userAccounts", updatedAccounts);

                    logger.info("Updated account balances in session for user ID: " + userId);
                }

                // Set success message
                request.getSession().setAttribute("transferSuccess", true);
                request.getSession().setAttribute("transferMessage", message);

                // Redirect to transfer.jsp
                response.sendRedirect("transfer.jsp");
            } else {
                request.setAttribute("errors", new ObjectMapper().writeValueAsString(errors));
                request.getRequestDispatcher("transfer.jsp").forward(request, response);
            }
        } catch (TransactionException te) {
            logger.severe("Transaction error: " + te.getMessage());
            errors.put("general", te.getMessage());
            request.setAttribute("errors", new ObjectMapper().writeValueAsString(errors));
            request.getRequestDispatcher("transfer.jsp").forward(request, response);
        } catch (Exception e) {
            logger.severe("Error processing transfer: " + e.getMessage());
            errors.put("general", "An unexpected error occurred: " + e.getMessage());
            request.setAttribute("errors", new ObjectMapper().writeValueAsString(errors));
            request.getRequestDispatcher("transfer.jsp").forward(request, response);
        }
    }

    private String determineAction(HttpServletRequest request) {
        if (request.getParameter("fromAccount") != null) {
            return "standard";
        } else if (request.getParameter("fromAccountSch") != null) {
            return "scheduled";
        }
        return "unknown";
    }

    private boolean processStandardTransfer(HttpServletRequest request, Map<String, String> errors) {
        // Extract parameters
        String fromAccount = request.getParameter("fromAccount");
        String toAccount = request.getParameter("toAccountNumber");
        String amountStr = request.getParameter("amount");
        String note = request.getParameter("transferNote");

        // Validate parameters
        if (isEmpty(fromAccount)) {
            errors.put("fromAccount", "Source account is required");
        }
        if (isEmpty(toAccount)) {
            errors.put("toAccountNumber", "Destination account is required");
        }

        BigDecimal amount = validateAmount(amountStr, errors);

        // Return if there are validation errors
        if (!errors.isEmpty()) {
            return false;
        }

        // Execute transfer using EJB
        return transferMoneyBean.transferFunds(fromAccount, toAccount, amount, note);
    }

    private boolean processScheduledTransfer(HttpServletRequest request, Map<String, String> errors) {
        // Extract parameters
        String fromAccount = request.getParameter("fromAccountSch");
        String toAccount = request.getParameter("toAccountNumberSch");
        String amountStr = request.getParameter("amountSch");
        String dateTimeStr = request.getParameter("startDate");
        String note = request.getParameter("transferNoteSch");

        // Validate parameters
        if (isEmpty(fromAccount)) {
            errors.put("fromAccountSch", "Source account is required");
        }
        if (isEmpty(toAccount)) {
            errors.put("toAccountNumberSch", "Destination account is required");
        }

        BigDecimal amount = validateAmount(amountStr, errors);
        LocalDateTime scheduledDateTime = validateDateTime(dateTimeStr, errors);

        // Return if there are validation errors
        if (!errors.isEmpty()) {
            return false;
        }

        // Schedule transfer using EJB
        return transferMoneyBean.scheduleTransfer(fromAccount, toAccount, amount, scheduledDateTime, note);
    }

    private BigDecimal validateAmount(String amountStr, Map<String, String> errors) {
        if (isEmpty(amountStr)) {
            errors.put("amount", "Amount is required");
            return null;
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr.replace(",", ""));
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                errors.put("amount", "Amount must be greater than zero");
                return null;
            }
            return amount;
        } catch (NumberFormatException e) {
            errors.put("amount", "Invalid amount format");
            return null;
        }
    }

    private LocalDateTime validateDateTime(String dateTimeStr, Map<String, String> errors) {
        if (isEmpty(dateTimeStr)) {
            errors.put("startDate", "Schedule date is required");
            return null;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);

            if (dateTime.isBefore(LocalDateTime.now())) {
                errors.put("startDate", "Schedule date must be in the future");
                return null;
            }

            return dateTime;
        } catch (DateTimeParseException e) {
            errors.put("startDate", "Invalid date format");
            return null;
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}