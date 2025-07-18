package com.bimalsha.ee.bank.customer.ejb;

import com.bimalsha.ee.bank.customer.ejb.remote.validateAccount;
import com.bimalsha.ee.bank.entity.Account;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class ValidateAccountBean implements validateAccount {

    @PersistenceContext(unitName = "NationalB")
    private EntityManager entityManager;

    @Override
    public Account validateAccountNumber(String accountNumber) {
        try {
            TypedQuery<Account> query = entityManager.createQuery(
                    "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber",
                    Account.class);
            query.setParameter("accountNumber", accountNumber);
            List<Account> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}