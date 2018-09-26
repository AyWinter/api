package com.mash.api.repository;

import com.mash.api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    public Account findByMobileNoAndPassword(String mobileNo, String password);

    public Account findByMobileNo(String mobileNo);

    @Modifying
    @Transactional
    @Query("delete from Account where id = ?1")
    public void deleteById(Integer id);
}
