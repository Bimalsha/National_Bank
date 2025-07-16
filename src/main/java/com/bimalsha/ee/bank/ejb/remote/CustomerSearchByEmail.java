package com.bimalsha.ee.bank.ejb.remote;

import java.util.List;

public interface CustomerSearchByEmail {
    Object findCustomerByEmail(String email);

    List<Object> searchCustomersByEmail(String emailPattern);

    Object findCustomerByContactNumber(String contactNumber);

    List<Object> searchCustomersByContactNumber(String numberPattern);

    // New methods for account number search
    Object findCustomerByAccountNumber(String accountNumber);

    List<Object> searchCustomersByAccountNumber(String accountNumberPattern);

    List<Object> searchCustomers(String searchTerm);
}