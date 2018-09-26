package com.mash.api.repository;

import com.mash.api.entity.Execute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ExecuteRepository extends JpaRepository<Execute, Integer>{

    Page<Execute> findAll(Specification<Execute> specification, Pageable pageable);

    Execute findByScheduleId(Integer scheduleId);

    Execute findById(Integer id);

    /**
     * 设置安装工人
     * @param id
     * @param worker
     */
    @Transactional
    @Query(value="update execute set worker=?2, state = 1 where id=?1", nativeQuery = true)
    @Modifying
    void setWorker(Integer id, String worker);

    @Query(value="select * from execute where worker_account_id = ?1 order by plan_install_time desc", nativeQuery = true)
    List<Execute> findByWorkerAccountId(Integer accountId);
}
