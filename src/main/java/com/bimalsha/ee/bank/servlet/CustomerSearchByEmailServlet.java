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

@WebServlet("/customerSearchByEmail")
public class CustomerSearchByEmailServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(CustomerSearchByEmailServlet.class.getName());

    @EJB
    private CustomerSearchByEmail customerSearchBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchType = request.getParameter("searchType");
        String searchTerm = request.getParameter("searchTerm");

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Search term is required");
            return;
        }

        List<Object> searchResults;

        try {
            // Determine which search method to use based on the searchType parameter
            if ("email".equals(searchType)) {
                searchResults = customerSearchBean.searchCustomersByEmail(searchTerm);
            } else if ("contact".equals(searchType)) {
                searchResults = customerSearchBean.searchCustomersByContactNumber(searchTerm);
            } else {
                // Default to general search
                searchResults = customerSearchBean.searchCustomers(searchTerm);
            }

            // Convert search results to JSON
            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

            for (Object result : searchResults) {
                if (result instanceof User) {
                    User user = (User) result;
                    JsonObjectBuilder userJson = Json.createObjectBuilder()
                            .add("id", user.getId())
                            .add("name", user.getName() != null ? user.getName() : "")
                            .add("email", user.getEmail() != null ? user.getEmail() : "")
                            .add("contact", user.getContact() != null ? user.getContact() : "");

                    jsonArrayBuilder.add(userJson);
                }
            }

            // Set response type and write JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonArrayBuilder.build().toString());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error searching for customers", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error processing search request: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getServletPath();

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
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("User not found");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid user ID format");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error selecting customer", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error processing selection request: " + e.getMessage());
            }
        } else {
            // Forward to doGet for search operations
            doGet(request, response);
        }
    }

    // Helper method to get a user by ID - this would typically use another method in your EJB
    private User getUserById(int id) {
        try {
            // This is a placeholder - you would need to implement a proper method to get user by ID
            // For now, we're doing a general search and filtering the results
            List<Object> allUsers = customerSearchBean.searchCustomers(String.valueOf(id));

            for (Object obj : allUsers) {
                if (obj instanceof User) {
                    User user = (User) obj;
                    if (user.getId() == id) {
                        return user;
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding user by ID: " + id, e);
        }
        return null;
    }
}