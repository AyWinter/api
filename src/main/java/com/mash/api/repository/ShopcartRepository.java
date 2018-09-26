package com.mash.api.repository;

import com.mash.api.entity.Shopcart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ShopcartRepository extends JpaRepository<Shopcart, Integer>{

    List<Shopcart> findByAccountId(Integer id);

    List<Shopcart> findByAccountIdAndSelected(Integer id, boolean selected);

    @Transactional
    @Query(value="delete from shopcart where account_id = ?1", nativeQuery = true)
    @Modifying
    void deleteByAccountId(Integer accountId);

    @Transactional
    @Query(value="update shopcart set selected = ?1 where id = ?2", nativeQuery = true)
    @Modifying
    void updateSelected(boolean selected, Integer id);

    @Transactional
    @Query(value="delete from shopcart where account_id = ?1 and product_id=?2", nativeQuery = true)
    @Modifying
    void deleteByAccountIdAndProductId(Integer accountId, Integer productId);
}
