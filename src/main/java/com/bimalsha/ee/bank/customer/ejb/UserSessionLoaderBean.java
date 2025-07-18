package com.bimalsha.ee.bank.customer.ejb;

import com.bimalsha.ee.bank.customer.ejb.remote.UserSessionLoader;
import com.bimalsha.ee.bank.entity.User;
import com.bimalsha.ee.bank.entity.Account;
import jakarta.ejb.Stateless;
import jakarta.ejb.Remote;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@Remote(UserSessionLoader.class)
public class UserSessionLoaderBean implements UserSessionLoader {
    private static final Logger logger = Logger.getLogger(UserSessionLoaderBean.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User loadUserById(Integer userId) {
        logger.log(Level.INFO, "Loading user with ID: {0}", userId);
        try {
            return entityManager.find(User.class, userId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading user by ID: " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<Account> loadUserAccounts(Integer userId) {
        logger.log(Level.INFO, "Loading accounts for user ID: {0}", userId);
        try {
            TypedQuery<Account> query = entityManager.createQuery(
                    "SELECT a FROM Account a WHERE a.user.id = :userId", Account.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading accounts for user: " + e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public Integer getUserIdByEmail(String email) {
        logger.log(Level.INFO, "Looking up user ID by email: {0}", email);
        try {
            TypedQuery<Integer> query = entityManager.createQuery(
                    "SELECT u.id FROM User u WHERE u.email = :email", Integer.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error finding user by email: " + e.getMessage(), e);
            return null;
        }
    }
}