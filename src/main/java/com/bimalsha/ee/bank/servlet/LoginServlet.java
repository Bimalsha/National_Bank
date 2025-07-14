package com.bimalsha.ee.bank.servlet;

import com.bimalsha.ee.bank.ejb.remote.LoginInterface;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @EJB
    private LoginInterface loginBean;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("username");
        String password = request.getParameter("password");

        // Use authenticate method which returns the user role
        String userRole = loginBean.authenticate(email, password);
        System.out.println(email + " " + password + " " + userRole);

        if (userRole != null) {
            // Store user info in session
            HttpSession session = request.getSession();
            session.setAttribute("userEmail", email);
            session.setAttribute("userRole", userRole);
            session.setAttribute("authenticated", true);


            // Redirect based on role
            if ("EMPLOYEE".equals(userRole)) {
                response.sendRedirect("employee.jsp");
            } else {
                response.sendRedirect("home.jsp");
            }
        } else {
            // Authentication failed
            request.setAttribute("errorMessage", "Invalid email or password");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}