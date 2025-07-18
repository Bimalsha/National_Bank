package com.bimalsha.ee.bank.register.servlet;

import com.bimalsha.ee.bank.register.ejb.remote.RegisterUser;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/registerCustomer")
public class RegisterCustomer extends HttpServlet {

    @EJB
    private RegisterUser registerUser;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get form parameters
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String confirmEmail = request.getParameter("confirmEmail");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

        // Validate form data
        if (name == null || email == null || phone == null || password == null ||
                name.trim().isEmpty() || email.trim().isEmpty() ||
                phone.trim().isEmpty() || password.trim().isEmpty()) {

            request.setAttribute("errorMessage", "All fields are required");
            request.getRequestDispatcher("employee.jsp").forward(request, response);
            return;
        }

        // Validate email matching
        if (confirmEmail != null && !email.equals(confirmEmail)) {
            request.setAttribute("errorMessage", "Email addresses do not match");
            request.getRequestDispatcher("employee.jsp").forward(request, response);
            return;
        }

        // Check if email is available
        if (!registerUser.isEmailAvailable(email)) {
            request.setAttribute("errorMessage", "Email already registered");
            request.getRequestDispatcher("employee.jsp").forward(request, response);
            return;
        }

        // Register the customer
        boolean success = registerUser.registerCustomer(name, email, phone, password);

        if (success) {
            request.setAttribute("successMessage", "Customer registered successfully");
        } else {
            Map<String, String> status = registerUser.getRegistrationStatus();
            request.setAttribute("errorMessage", status.get("message"));
        }

        request.getRequestDispatcher("employee.jsp").forward(request, response);
    }
}