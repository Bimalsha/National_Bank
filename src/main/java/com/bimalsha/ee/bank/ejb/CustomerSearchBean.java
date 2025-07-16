package com.bimalsha.ee.bank.ejb;

import com.bimalsha.ee.bank.ejb.remote.CustomerSearchByEmail;
import com.bimalsha.ee.bank.entity.User;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class CustomerSearchBean implements CustomerSearchByEmail {

    private static final Logger logger = Logger.getLogger(CustomerSearchBean.class.getName());

    @PersistenceContext(unitName = "NationalB")
    private EntityManager entityManager;

    @Override
    public Object findCustomerByEmail(String email) {
        logger.log(Level.INFO, "Searching for customer with email: {0}", email);
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u JOIN u.userType ut WHERE u.email = :email AND ut.type = :userType", User.class);
            query.setParameter("email", email);
            query.setParameter("userType", "CUSTOMER");
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.log(Level.INFO, "No customer found with email: {0}", email);
            return null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error finding customer by email: " + email, e);
            return null;
        }
    }

    @Override
    public List<Object> searchCustomersByEmail(String emailPattern) {
        logger.log(Level.INFO, "Searching for customers with email pattern: {0}", emailPattern);
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u JOIN u.userType ut WHERE u.email LIKE :pattern AND ut.type = :userType", User.class);
            query.setParameter("pattern", "%" + emailPattern + "%");
            query.setParameter("userType", "CUSTOMER");
            List<User> users = query.getResultList();
            return new ArrayList<>(users);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error searching customers by email pattern: " + emailPattern, e);
            return new ArrayList<>();
        }
    }

    @Override
    public Object findCustomerByContactNumber(String contactNumber) {
        logger.log(Level.INFO, "Searching for customer with contact number: {0}", contactNumber);
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u JOIN u.userType ut WHERE u.contact = :contact AND ut.type = :userType", User.class);
            query.setParameter("contact", contactNumber);
            query.setParameter("userType", "CUSTOMER");
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.log(Level.INFO, "No customer found with contact number: {0}", contactNumber);
            return null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error finding customer by contact number: " + contactNumber, e);
            return null;
        }
    }

    @Override
    public List<Object> searchCustomersByContactNumber(String numberPattern) {
        logger.log(Level.INFO, "Searching for customers with contact number pattern: {0}", numberPattern);
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u JOIN u.userType ut WHERE u.contact LIKE :pattern AND ut.type = :userType", User.class);
            query.setParameter("pattern", "%" + numberPattern + "%");
            query.setParameter("userType", "CUSTOMER");
            List<User> users = query.getResultList();
            return new ArrayList<>(users);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error searching customers by contact number pattern: " + numberPattern, e);
            return new ArrayList<>();
        }
    }

    @Override
    public Object findCustomerByAccountNumber(String accountNumber) {
        logger.log(Level.INFO, "Searching for customer with account number: {0}", accountNumber);
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM Account a JOIN a.user u JOIN u.userType ut WHERE a.accountNumber = :accountNumber AND ut.type = :userType", User.class);
            query.setParameter("accountNumber", accountNumber);
            query.setParameter("userType", "CUSTOMER");
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.log(Level.INFO, "No customer found with account number: {0}", accountNumber);
            return null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error finding customer by account number: " + accountNumber, e);
            return null;
        }
    }

    @Override
    public List<Object> searchCustomersByAccountNumber(String accountNumberPattern) {
        logger.log(Level.INFO, "Searching for customers with account number pattern: {0}", accountNumberPattern);
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT DISTINCT u FROM Account a JOIN a.user u JOIN u.userType ut WHERE a.accountNumber LIKE :pattern AND ut.type = :userType", User.class);
            query.setParameter("pattern", "%" + accountNumberPattern + "%");
            query.setParameter("userType", "CUSTOMER");
            List<User> users = query.getResultList();
            return new ArrayList<>(users);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error searching customers by account number pattern: " + accountNumberPattern, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Object> searchCustomers(String searchTerm) {
        logger.log(Level.INFO, "Searching for customers with term: {0}", searchTerm);
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT DISTINCT u FROM User u LEFT JOIN Account a ON a.user = u JOIN u.userType ut " +
                            "WHERE (u.email LIKE :pattern OR u.contact LIKE :pattern OR u.name LIKE :pattern OR a.accountNumber LIKE :pattern) " +
                            "AND ut.type = :userType", User.class);
            query.setParameter("pattern", "%" + searchTerm + "%");
            query.setParameter("userType", "CUSTOMER");
            List<User> users = query.getResultList();
            return new ArrayList<>(users);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error searching customers by term: " + searchTerm, e);
            return new ArrayList<>();
        }
    }
}