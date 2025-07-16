package com.bimalsha.ee.bank.servlet;

import com.bimalsha.ee.bank.ejb.remote.CustomerSearchByEmail;
import com.bimalsha.ee.bank.entity.User;
import jakarta.ejb.EJB;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/customerSearchByEmail", "/selectCustomer"})
public class CustomerSearchByEmailServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(CustomerSearchByEmailServlet.class.getName());

    @EJB
    private CustomerSearchByEmail customerSearchBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchType = request.getParameter("searchType");
        String searchTerm = request.getParameter("searchTerm");

        logger.log(Level.INFO, "Search request received - Type: {0}, Term: {1}",
                new Object[]{searchType, searchTerm});

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Search term is required");
            return;
        }

        List<Object> searchResults;

        try {
            // Determine which search method to use based on the searchType parameter
            if ("email".equals(searchType)) {
                logger.log(Level.INFO, "Performing email search with term: {0}", searchTerm);
                searchResults = customerSearchBean.searchCustomersByEmail(searchTerm);
            } else if ("contact".equals(searchType)) {
                logger.log(Level.INFO, "Performing contact search with term: {0}", searchTerm);
                searchResults = customerSearchBean.searchCustomersByContactNumber(searchTerm);
            } else if ("account".equals(searchType)) {
                logger.log(Level.INFO, "Performing account number search with term: {0}", searchTerm);
                searchResults = customerSearchBean.searchCustomersByAccountNumber(searchTerm);
                logger.log(Level.INFO, "Account number search returned {0} results",
                        searchResults != null ? searchResults.size() : 0);
            } else {
                logger.log(Level.INFO, "Performing general search with term: {0}", searchTerm);
                searchResults = customerSearchBean.searchCustomers(searchTerm);
            }

            // Convert search results to JSON
            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
            int resultCount = 0;

            for (Object result : searchResults) {
                if (result instanceof User) {
                    resultCount++;
                    User user = (User) result;
                    JsonObjectBuilder userJson = Json.createObjectBuilder()
                            .add("id", user.getId())
                            .add("name", user.getName() != null ? user.getName() : "")
                            .add("email", user.getEmail() != null ? user.getEmail() : "")
                            .add("contact", user.getContact() != null ? user.getContact() : "");

                    jsonArrayBuilder.add(userJson);
                }
            }

            logger.log(Level.INFO, "Search completed. Returning {0} results", resultCount);

            // Set response type and write JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonArrayBuilder.build().toString());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error searching for customers: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error processing search request: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();
        logger.log(Level.INFO, "POST request received with path: {0}", action);

        if ("/selectCustomer".equals(action)) {
            // Handle selecting a customer for the Open Account section
            String userId = request.getParameter("userId");

            if (userId == null || userId.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("User ID is required");
                return;
            }

            try {
                // Find the user by ID
                int id = Integer.parseInt(userId);
                User user = getUserById(id);

                if (user != null) {
                    // Store selected user in session for use in the Open Account form
                    HttpSession session = request.getSession();
                    session.setAttribute("selectedUser", user);
                    logger.log(Level.INFO, "User selected and stored in session: {0}", user.getId());

                    // Return user details as JSON
                    JsonObjectBuilder userJson = Json.createObjectBuilder()
                            .add("id", user.getId())
                            .add("name", user.getName() != null ? user.getName() : "")
                            .add("email", user.getEmail() != null ? user.getEmail() : "")
                            .add("contact", user.getContact() != null ? user.getContact() : "");

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(userJson.build().toString());
                } else {
                    logger.log(Level.WARNING, "User not found with ID: {0}", id);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("User not found");
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Invalid user ID format: {0}", userId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid user ID format");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error selecting customer: " + e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error processing selection request: " + e.getMessage());
            }
        } else {
            // Forward to doGet for search operations
            logger.log(Level.INFO, "Forwarding to doGet for search operation");
            doGet(request, response);
        }
    }

    private User getUserById(int id) {
        try {
            logger.log(Level.INFO, "Looking up user by ID: {0}", id);
            // This is a placeholder - you would need to implement a proper method to get user by ID
            // For now, we're doing a general search and filtering the results
            List<Object> allUsers = customerSearchBean.searchCustomers(String.valueOf(id));

            for (Object obj : allUsers) {
                if (obj instanceof User) {
                    User user = (User) obj;
                    if (user.getId() == id) {
                        logger.log(Level.INFO, "Found user with ID: {0}", id);
                        return user;
                    }
                }
            }

            logger.log(Level.INFO, "No user found with ID: {0}", id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding user by ID: " + id, e);
        }
        return null;
    }
}