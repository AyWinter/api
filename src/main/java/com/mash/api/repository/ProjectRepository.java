package com.mash.api.repository;

import com.mash.api.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Integer>{

    /**
     * 条件查询
     * @param specification
     * @param pageable
     * @return
     */
    Page<Project> findAll(Specification<Project> specification, Pageable pageable);
}
