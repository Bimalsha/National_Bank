package com.bimalsha.ee.bank.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/home.jsp", "/transfer.jsp"})
public class CustomerAuthFilter implements Filter {

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

        boolean isAuthenticated = false;
        boolean isCustomer = false;

        if (session != null) {
            Boolean authenticated = (Boolean) session.getAttribute("authenticated");
            String userRole = (String) session.getAttribute("userRole");

            isAuthenticated = (authenticated != null && authenticated);
            isCustomer = "CUSTOMER".equals(userRole);
        }

        if (isAuthenticated && isCustomer) {
            // User is authenticated and has CUSTOMER role
            chain.doFilter(request, response);
        } else {
            // User is not authenticated or not a customer
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.jsp");
        }
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}