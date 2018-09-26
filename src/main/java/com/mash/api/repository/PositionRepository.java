package com.mash.api.repository;

import com.mash.api.entity.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Integer> {

    /**
     * 条件查询
     * @param specification
     * @param pageable
     * @return
     */
    Page<Position> findAll(Specification<Position> specification, Pageable pageable);

    List<Position> findAll(Specification<Position> specification);

    @Query(value="select id from position where vendor_id=?1 and area=?2", nativeQuery = true)
    List<Integer> findPositionIdsByVendorIdAndArea(Integer vendorId, String area);

    @Query(value="select * from position where vendor_id=?1 limit 0, 3", nativeQuery = true)
    List<Position> hotPosition(Integer vendorId);
}
