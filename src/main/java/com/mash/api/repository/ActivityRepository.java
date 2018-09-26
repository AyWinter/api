package com.mash.api.repository;

import com.mash.api.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Integer>{

    List<Activity> findByAccountId(Integer accountId);

    @Query(value="select * from activity where end_time > now() and account_id = ?1 ", nativeQuery = true)
    List<Activity> findByAccountId2(Integer accountId);

    Page<Activity> findByAccountId(Pageable pageable, Integer accountId);
}
