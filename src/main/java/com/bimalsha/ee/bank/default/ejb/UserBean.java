package com.bimalsha.ee.bank.ejb;

import com.bimalsha.ee.bank.entity.User;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class UserBean {

    @PersistenceContext
    private EntityManager em;

    @PermitAll
    public User findUserByEmail(String email) {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        return query.getSingleResult();
    }

    @RolesAllowed("EMPLOYEE")
    public List<User> getAllUsers() {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
        return query.getResultList();
    }

    @RolesAllowed({"CUSTOMER", "EMPLOYEE"})
    public User getUserDetails(int userId) {
        return em.find(User.class, userId);
    }

    @DenyAll
    public void deleteAllUsers() {
        // This method cannot be accessed by anyone due to @DenyAll
    }
}