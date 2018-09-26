package com.mash.api.repository;

import com.mash.api.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface RefundRepository extends JpaRepository<Refund, Integer>{

    @Query(value="select * from refund where state=1 and vendor_id=?1", nativeQuery = true)
    List<Refund> findByVendorId(Integer vendorId);

    @Transactional
    @Query(value="update refund set operator=?2, explain_text=?3, operate_time=?4, state=?5 where id=?1", nativeQuery = true)
    @Modifying
    void updateRefundState(Integer id, String operator, String explainText, Date operateTime, Integer state);
}
