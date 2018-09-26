package com.mash.api.service.impl;

import com.mash.api.entity.Account;
import com.mash.api.repository.AccountRepository;
import com.mash.api.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService{

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account add(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Account findByMobileNoAndPassword(String mobileNo, String password) {
        return accountRepository.findByMobileNoAndPassword(mobileNo, password);
    }

    @Override
    public Account resetPassword(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Account findById(Integer id) {
        return accountRepository.findOne(id);
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account findByMobileNo(String mobileNo) {
        return accountRepository.findByMobileNo(mobileNo);
    }

    @Override
    public void deleteById(Integer id) {
        accountRepository.deleteById(id);
    }
}
