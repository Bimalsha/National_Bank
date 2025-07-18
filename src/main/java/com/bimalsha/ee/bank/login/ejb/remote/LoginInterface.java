package com.bimalsha.ee.bank.login.ejb.remote;

import jakarta.annotation.security.PermitAll;

public interface LoginInterface {
    @PermitAll
    boolean login(String email, String password);

    @PermitAll
    String authenticate(String email, String password);
}