package com.bimalsha.ee.bank.servlet;

import com.bimalsha.ee.bank.ejb.OpenAccountBean;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for handling account opening requests
 */
@WebServlet("/openAccount")
public class OpenAccountServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(OpenAccountServlet.class.getName());

    @EJB
    private OpenAccountBean openAccountBean;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Parse and validate input parameters
            String userIdParam = request.getParameter("userId");
            String accountType = request.getParameter("accountType");
            String initialDepositParam = request.getParameter("initialDeposit");

            logger.log(Level.INFO, "Received parameters: userId={0}, accountType={1}, initialDeposit={2}",
                    new Object[]{userIdParam, accountType, initialDepositParam});

            // Check for null or empty parameters
            if (userIdParam == null || userIdParam.trim().isEmpty() ||
                    accountType == null || accountType.trim().isEmpty() ||
                    initialDepositParam == null || initialDepositParam.trim().isEmpty()) {

                logger.log(Level.WARNING, "Missing required parameters");
                request.setAttribute("errorMessage", "Missing required parameters");
                request.getRequestDispatcher("/employee.jsp").forward(request, response);
                return;
            }

            Long userId;
            BigDecimal initialDeposit;

            try {
                userId = Long.parseLong(userIdParam.trim());
                initialDeposit = new BigDecimal(initialDepositParam.trim());
            } catch (NumberFormatException e) {
                logger.log(Level.SEVERE, "Invalid number format: {0}", e.getMessage());
                request.setAttribute("errorMessage", "Invalid user ID or deposit amount");
                request.getRequestDispatcher("/employee.jsp").forward(request, response);
                return;
            }

            // Validate deposit amount
            if (initialDeposit.compareTo(BigDecimal.ZERO) <= 0) {
                logger.log(Level.WARNING, "Invalid deposit amount: {0}", initialDeposit);
                request.setAttribute("errorMessage", "Initial deposit must be greater than zero");
                request.getRequestDispatcher("/employee.jsp").forward(request, response);
                return;
            }

            // Check eligibility and open account as before
            if (!openAccountBean.canOpenAccount(userId, accountType)) {
                request.setAttribute("errorMessage", "User is not eligible to open this type of account");
                request.getRequestDispatcher("/employee.jsp").forward(request, response);
                return;
            }

            // Open the account - updated to use AccountResult
            OpenAccountBean.AccountResult result = openAccountBean.openAccountForExistingCustomer(userId, accountType, initialDeposit);

            if (result != null) {
                logger.log(Level.INFO, "Account opened successfully: ID={0}, Number={1}",
                        new Object[]{result.getId(), result.getAccountNumber()});
                request.setAttribute("successMessage",
                        "Account opened successfully. Account ID: " + result.getId() +
                                ", Account Number: " + result.getAccountNumber());
            } else {
                logger.log(Level.WARNING, "Failed to open account");
                request.setAttribute("errorMessage", "Failed to open account. Please try again.");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing account opening request", e);
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
        }

        request.getRequestDispatcher("/employee.jsp").forward(request, response);
    }
}