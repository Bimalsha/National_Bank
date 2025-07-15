package com.bimalsha.ee.bank.ejb;

import com.bimalsha.ee.bank.ejb.remote.RegisterUser;
import com.bimalsha.ee.bank.entity.User;
import com.bimalsha.ee.bank.entity.UserType;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.HashMap;
import java.util.Map;

@Stateless
public class RegisterCustomerBean implements RegisterUser {

    @PersistenceContext
    private EntityManager entityManager;

    private Map<String, String> registrationStatus = new HashMap<>();

    @Override
    public boolean registerCustomer(String name, String email, String phone, String password) {
        registrationStatus.clear();

        // Check if email exists
        if (!isEmailAvailable(email)) {
            registrationStatus.put("status", "error");
            registrationStatus.put("message", "Email already registered");
            return false;
        }

        try {
            // Create new user
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setContact(phone);
            user.setPassword(password);

            // Set user type to CUSTOMER
            UserType customerType = entityManager.createQuery(
                            "SELECT ut FROM UserType ut WHERE ut.type = :type",
                            UserType.class)
                    .setParameter("type", "CUSTOMER")
                    .getSingleResult();

            user.setUserType(customerType);

            // Persist user
            entityManager.persist(user);

            registrationStatus.put("status", "success");
            registrationStatus.put("message", "Customer registered successfully");
            return true;
        } catch (Exception e) {
            registrationStatus.put("status", "error");
            registrationStatus.put("message", "Registration failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean registerEmployee(String name, String email, String phone, String password, String employeeId) {
        registrationStatus.clear();

        // Check if email exists
        if (!isEmailAvailable(email)) {
            registrationStatus.put("status", "error");
            registrationStatus.put("message", "Email already registered");
            return false;
        }

        try {
            // Create new user
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setContact(phone);
            user.setPassword(password);

            // Set user type to EMPLOYEE
            UserType employeeType = entityManager.createQuery(
                            "SELECT ut FROM UserType ut WHERE ut.type = :type",
                            UserType.class)
                    .setParameter("type", "EMPLOYEE")
                    .getSingleResult();

            user.setUserType(employeeType);

            // Persist user
            entityManager.persist(user);

            registrationStatus.put("status", "success");
            registrationStatus.put("message", "Employee registered successfully");
            return true;
        } catch (Exception e) {
            registrationStatus.put("status", "error");
            registrationStatus.put("message", "Registration failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isEmailAvailable(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
        query.setParameter("email", email);
        Long count = query.getSingleResult();
        return count == 0;
    }

    public Map<String, String> getRegistrationStatus() {
        return registrationStatus;
    }
}