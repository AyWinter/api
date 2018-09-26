package com.mash.api.service;

import com.mash.api.entity.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PositionService {

    Position save(Position position);

    /**
     * 条件查询
     * @param specification
     * @param pageable
     * @return
     */
    Page<Position> findAll(Specification<Position> specification, Pageable pageable);

    List<Position> findAll(Specification<Position> specification);

    Position findById(Integer id);

    List<Integer> findPositionIdsByVendorIdAndArea(Integer vendorId, String area);

    List<Position> hotPosition(Integer vendorId);
}
