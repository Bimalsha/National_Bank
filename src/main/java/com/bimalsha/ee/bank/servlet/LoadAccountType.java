package com.bimalsha.ee.bank.servlet;

import com.bimalsha.ee.bank.ejb.AccountTypeBean;
import com.bimalsha.ee.bank.ejb.remote.AccountType;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/loadAccountTypes")
public class LoadAccountType extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Define all available account types based on constants
        List<String> accountTypes = Arrays.asList(
                AccountType.CHECKING,
                AccountType.SAVINGS,
                AccountType.FIXED_DEPOSIT,
                AccountType.CREADIT  // Note: There's a typo in AccountTypeBean (CREADIT)
        );

        // Create JSON response
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        for (String type : accountTypes) {
            JsonObject accountTypeJson = Json.createObjectBuilder()
                    .add("id", generateId(type))
                    .add("type", type)
                    .add("displayName", getDisplayName(type))
                    .build();
            arrayBuilder.add(accountTypeJson);
        }

        // Set response type and write JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Json.createWriter(response.getWriter()).write(arrayBuilder.build());
    }

    // Helper method to generate an ID for demo purposes
    private String generateId(String type) {
        return "ACC_" + type.toUpperCase().substring(0, 3);
    }

    // Helper method to get display names for account types
    private String getDisplayName(String type) {
        switch (type) {
            case AccountType.CHECKING:
                return "Checking Account";
            case AccountType.SAVINGS:
                return "Savings Account";
            case AccountType.FIXED_DEPOSIT:
                return "Fixed Deposit Account";
            case AccountType.CREADIT:
                return "Credit Account";
            default:
                return type;
        }
    }
}
