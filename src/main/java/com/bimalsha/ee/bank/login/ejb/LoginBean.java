package com.bimalsha.ee.bank.login.ejb;

import com.bimalsha.ee.bank.login.ejb.remote.LoginInterface;
import com.bimalsha.ee.bank.entity.User;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
public class LoginBean implements LoginInterface {

    @PersistenceContext
    private EntityManager em;

    @Override
    @PermitAll
    public boolean login(String email, String password) {
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email AND u.password = :password",
                    User.class);
            query.setParameter("email", email);
            query.setParameter("password", password);

            User user = query.getSingleResult();
            return user != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    @PermitAll
    public String authenticate(String email, String password) {
        try {
            // Use JOIN FETCH to ensure userType is loaded
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u LEFT JOIN FETCH u.userType WHERE u.email = :email AND u.password = :password",
                    User.class);
            query.setParameter("email", email);
            query.setParameter("password", password);

            User user = query.getSingleResult();

            // Add null checks to prevent NPE
            if (user != null) {
                if (user.getUserType() != null) {
                    return user.getUserType().getType();
                } else {
                    // Default role if userType is null
                    return "CUSTOMER";
                }
            }
            return null;
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error in authenticate method: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}