package com.bimalsha.ee.bank.ejb.remote;

import java.util.List;


public interface CustomerSearchByEmail {


    Object findCustomerByEmail(String email);


    List<Object> searchCustomersByEmail(String emailPattern);


    Object findCustomerByContactNumber(String contactNumber);


    List<Object> searchCustomersByContactNumber(String numberPattern);


    List<Object> searchCustomers(String searchTerm);
}