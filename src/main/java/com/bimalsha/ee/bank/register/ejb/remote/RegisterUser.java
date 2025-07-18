package com.bimalsha.ee.bank.register.ejb.remote;

import java.util.Map;

public interface RegisterUser {

    boolean registerCustomer(String name, String email, String phone, String password);


    boolean registerEmployee(String name, String email, String phone, String password, String employeeId);


    boolean isEmailAvailable(String email);

    Map<String, String> getRegistrationStatus();
}