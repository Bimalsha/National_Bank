package com.bimalsha.ee.bank.ejb.remote;

import com.bimalsha.ee.bank.entity.User;
import com.bimalsha.ee.bank.entity.Account;
import java.util.List;

public interface UserSessionLoader {
    User loadUserById(Integer userId);
    List<Account> loadUserAccounts(Integer userId);
    Integer getUserIdByEmail(String email);
}