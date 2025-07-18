package com.bimalsha.ee.bank.filter;

import com.bimalsha.ee.bank.entity.User;
import com.bimalsha.ee.bank.entity.UserType;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/index.jsp", "/", "/index"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        // Check if user is already logged in
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            UserType userType = user.getUserType();

            // Redirect based on user type
            if (userType != null) {
                String type = userType.getType();
                if ("CUSTOMER".equalsIgnoreCase(type)) {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/home.jsp");
                    return;
                } else if ("EMPLOYEE".equalsIgnoreCase(type)) {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/employee.jsp");
                    return;
                }
            }
        }

        // Continue the filter chain (allow access to index.jsp)
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}