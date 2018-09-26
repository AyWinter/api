package com.mash.api.service;

import com.mash.api.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProjectService {

    Project save(Project project);

    /**
     * 条件查询
     * @param specification
     * @param pageable
     * @return
     */
    Page<Project> findAll(Specification<Project> specification, Pageable pageable);

    Project findById(Integer id);

    void deleteById(Integer id);
}
