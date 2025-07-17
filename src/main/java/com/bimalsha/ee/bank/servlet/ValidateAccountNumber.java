package com.bimalsha.ee.bank.servlet;

import com.bimalsha.ee.bank.ejb.remote.validateAccount;
import com.bimalsha.ee.bank.entity.Account;
import com.bimalsha.ee.bank.entity.User;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/validateAccount")
public class ValidateAccountNumber extends HttpServlet {

    @EJB
    private validateAccount validateAccountEJB;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNumber = request.getParameter("accountNumber");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                out.write("{\"valid\": false, \"message\": \"Account number is required\"}");
                return;
            }

            // Find the account using the EJB
            Account account = validateAccountEJB.validateAccountNumber(accountNumber);

            if (account != null && account.getUser() != null) {
                // Account found, return holder name
                User accountHolder = account.getUser();
                String holderName = accountHolder.getName() != null ? accountHolder.getName() : "Unknown";

                out.write("{\"valid\": true, \"accountHolderName\": \"" + escapeJson(holderName) + "\"}");
            } else {
                // Account not found or has no associated user, allow manual entry
                out.write("{\"valid\": false, \"message\": \"Account not found. Please enter recipient name manually.\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"valid\": false, \"message\": \"Error: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}