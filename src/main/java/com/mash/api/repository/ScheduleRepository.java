package com.mash.api.repository;

import com.mash.api.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer>{

    Schedule save(Schedule schedule);

    @Transactional
    @Query(value="update schedule set state=?2 where id=?1", nativeQuery = true)
    @Modifying
    void updateState(Integer id, Integer state);

    /**
     * 排期单分页条件查询
     * @param specification
     * @param pageable
     * @return
     */
    Page<Schedule> findAll(Specification<Schedule> specification, Pageable pageable);

    /**
     * 查询排期单根据Number
     * @param number
     * @return
     */
    Schedule findByNumber(String number);
}
