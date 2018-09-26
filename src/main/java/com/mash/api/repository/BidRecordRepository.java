package com.mash.api.repository;

import com.mash.api.entity.BidRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BidRecordRepository extends JpaRepository<BidRecord, Integer>{

    @Query(value="select * from bid_record where product_id = ?1 order by amount desc limit 1", nativeQuery = true)
    BidRecord findHighestPrice(Integer productId);

    @Transactional
    @Query(value="update bid_record set state = 1 where product_id = ?1 and id != ?2", nativeQuery = true)
    @Modifying
    void updateBidRecordState(Integer productId, Integer currentBidRecordId);

    @Query(value="select * from bid_record where product_id = ?1 order by amount desc", nativeQuery = true)
    List<BidRecord> findByProductId(Integer productId);
}
