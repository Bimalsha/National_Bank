package com.bimalsha.ee.bank.customer.servlet;

import com.bimalsha.ee.bank.entity.User;
import com.bimalsha.ee.bank.entity.Account;
import com.bimalsha.ee.bank.customer.ejb.remote.UserSessionLoader;
import jakarta.ejb.EJB;
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

@WebServlet("/loadUserSession")
public class UserSessionLoaderServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(UserSessionLoaderServlet.class.getName());

    @EJB
    private UserSessionLoader userSessionLoader;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.log(Level.WARNING, "No active session found");
            handleErrorResponse(request, response, "No active session");
            return;
        }

        // Debugging - log all session attributes
        logger.log(Level.INFO, "Session attributes: ");
        java.util.Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            logger.log(Level.INFO, "  " + name + ": " + session.getAttribute(name));
        }

        // Try multiple sources to get userId
        Integer userId = (Integer) session.getAttribute("userId");
        String userEmail = (String) session.getAttribute("userEmail");
        User user = (User) session.getAttribute("user");

        // If user object exists but userId doesn't, get it from user
        if (userId == null && user != null) {
            userId = user.getId();
            session.setAttribute("userId", userId);
            logger.log(Level.INFO, "Set userId in session from user object: {0}", userId);
        }

        if (userId == null) {
            // Try from parameter
            String userIdParam = request.getParameter("userId");
            if (userIdParam != null && !userIdParam.isEmpty()) {
                try {
                    userId = Integer.parseInt(userIdParam);
                    session.setAttribute("userId", userId);
                    logger.log(Level.INFO, "Set userId in session from parameter: {0}", userId);
                } catch (NumberFormatException e) {
                    logger.log(Level.WARNING, "Invalid userId parameter: " + userIdParam);
                }
            }

            // Try from email lookup
            if (userId == null && userEmail != null) {
                userId = userSessionLoader.getUserIdByEmail(userEmail);
                if (userId != null) {
                    session.setAttribute("userId", userId);
                    logger.log(Level.INFO, "Set userId in session from email: {0}", userId);
                }
            }
        }

        if (userId == null) {
            logger.log(Level.WARNING, "User ID not found in session or request parameters");
            handleErrorResponse(request, response, "User not authenticated");
            return;
        }

        // Load user data
        user = userSessionLoader.loadUserById(userId);
        if (user == null) {
            logger.log(Level.WARNING, "Could not load user with ID: {0}", userId);
            handleErrorResponse(request, response, "User data not found");
            return;
        }

        // Load user accounts
        List<Account> accounts = userSessionLoader.loadUserAccounts(userId);

        // Store in session
        session.setAttribute("user", user);
        session.setAttribute("userAccounts", accounts);
        session.setAttribute("userId", userId); // Ensure userId is in session
        session.setAttribute("userEmail", user.getEmail()); // Save email for future lookups

        logger.log(Level.INFO, "User session loaded successfully for user ID: {0}", userId);

        // Handle the response appropriately
        handleSuccessResponse(request, response);
    }

    private void handleErrorResponse(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        String ajaxHeader = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(ajaxHeader)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{ \"success\": false, \"error\": \"" + message + "\" }");
        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    private void handleSuccessResponse(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String ajaxHeader = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(ajaxHeader)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{ \"success\": true }");
        } else {
            response.sendRedirect(request.getContextPath() + "/home.jsp");
        }
    }
}