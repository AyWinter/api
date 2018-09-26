package com.mash.api.repository;

import com.mash.api.entity.Demand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DemandRepository extends JpaRepository<Demand, Integer>{

    @Query(value = "select * from demand order by publish_time desc", nativeQuery = true)
    List<Demand> findAllOrderByPublishTimeDesc();

    @Query(value = "select * from demand where account_id=?1 order by publish_time desc", nativeQuery = true)
    List<Demand> findByAccountId(Integer accountId);
}
