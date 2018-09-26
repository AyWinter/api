package com.mash.api.service;

import com.mash.api.entity.Account;

import java.util.List;

public interface AccountService {

    Account add(Account account);
    Account findByMobileNo(String mobileNo);
    Account findByMobileNoAndPassword(String mobileNo, String password);
    Account resetPassword(Account account);
    Account findById(Integer id);
    List<Account> findAll();
    void deleteById(Integer id);
}
