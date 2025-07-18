package com.bimalsha.ee.bank.servlet;

import com.bimalsha.ee.bank.ejb.remote.TransactionHistoryLoader;
import com.bimalsha.ee.bank.entity.TransactionHistory;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet("/transactionHistory")
public class TransactionHistoryServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(TransactionHistoryServlet.class.getName());

    @EJB
    private TransactionHistoryLoader transactionHistoryLoader;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String accountNumber = request.getParameter("accountNumber");
        String transactionType = request.getParameter("type"); // all, sent, received
        String limitParam = request.getParameter("limit");

        List<TransactionHistory> transactions;

        if (accountNumber != null && !accountNumber.isEmpty()) {
            // Load transactions for specific account
            transactions = transactionHistoryLoader.loadAccountTransactions(accountNumber);
            request.setAttribute("accountNumber", accountNumber);
        } else if ("sent".equals(transactionType)) {
            // Load only sent transactions
            transactions = transactionHistoryLoader.loadUserSentTransactions(userId);
            request.setAttribute("transactionType", "sent");
        } else if ("received".equals(transactionType)) {
            // Load only received transactions
            transactions = transactionHistoryLoader.loadUserReceivedTransactions(userId);
            request.setAttribute("transactionType", "received");
        } else {
            // Load all transactions
            transactions = transactionHistoryLoader.loadUserTransactions(userId);
            request.setAttribute("transactionType", "all");
        }

        // Apply limit if specified
        if (limitParam != null && !limitParam.isEmpty()) {
            try {
                int limit = Integer.parseInt(limitParam);
                if (limit > 0 && limit < transactions.size()) {
                    transactions = transactions.subList(0, limit);
                }
            } catch (NumberFormatException e) {
                // Ignore invalid limit parameter
            }
        }

        request.setAttribute("transactions", transactions);

        // Handle AJAX requests
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            try {
                // Convert transactions to DTOs to avoid lazy loading issues
                List<Map<String, Object>> transactionDTOs = new ArrayList<>();

                for (TransactionHistory tx : transactions) {
                    Map<String, Object> txDTO = new HashMap<>();
                    txDTO.put("id", tx.getId());
                    txDTO.put("amount", tx.getAmount());
                    txDTO.put("dateTime", tx.getDateTime());

                    // Add default/derived values for missing fields
                    txDTO.put("type", "Transfer"); // Default type for all transactions
                    txDTO.put("notes", tx.getReason()); // Use reason as notes
                    txDTO.put("completed", true); // Default all transactions to completed

                    // Only include necessary account information
                    Map<String, Object> fromAccount = new HashMap<>();
                    fromAccount.put("accountNumber", tx.getFromAccount().getAccountNumber());
                    if (tx.getFromAccount().getUser() != null) {
                        Map<String, Object> fromUser = new HashMap<>();
                        fromUser.put("id", tx.getFromAccount().getUser().getId());
                        fromUser.put("name", tx.getFromAccount().getUser().getName());
                        fromAccount.put("user", fromUser);
                    }

                    Map<String, Object> toAccount = new HashMap<>();
                    toAccount.put("accountNumber", tx.getToAccount().getAccountNumber());
                    if (tx.getToAccount().getUser() != null) {
                        Map<String, Object> toUser = new HashMap<>();
                        toUser.put("id", tx.getToAccount().getUser().getId());
                        toUser.put("name", tx.getToAccount().getUser().getName());
                        toAccount.put("user", toUser);
                    }

                    txDTO.put("fromAccount", fromAccount);
                    txDTO.put("toAccount", toAccount);

                    transactionDTOs.add(txDTO);
                }

                // Prepare response with simplified data
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("transactions", transactionDTOs);
                responseData.put("currentUserId", userId);

                new ObjectMapper().writeValue(response.getWriter(), responseData);
            } catch (Exception e) {
                logger.severe("Error serializing transaction data: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Failed to load transaction data\"}");
            }
        } else {
            request.getRequestDispatcher("transaction-history.jsp").forward(request, response);
        }
    }
}