package com.mash.api.repository;

import com.mash.api.entity.BidResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BidResultRepository extends JpaRepository<BidResult, Integer>{

    @Transactional
    @Query(value="update bid_result set state = ?2 where id=?1", nativeQuery = true)
    @Modifying
    void updateState(Integer id, Integer state);

    @Transactional
    @Query(value="update bid_result set state = ?2 where product_id=?1", nativeQuery = true)
    @Modifying
    void updateState2(Integer productId, Integer state);

    BidResult findByProductId(Integer productId);

    List<BidResult> findByAccountIdAndState(Integer accountId, Integer state);

    BidResult findByProductIdAndAccountId(Integer productId, Integer accountId);
}
