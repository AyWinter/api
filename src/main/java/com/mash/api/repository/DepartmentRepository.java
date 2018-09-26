package com.mash.api.repository;

import com.mash.api.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Integer>{

    List<Department> findByEnterpriseId(Integer enterpriseId);

    @Transactional
    @Query(value="update department set rights=?2, menu_ids=?3 where id=?1", nativeQuery = true)
    @Modifying
    void updateRights(Integer id, String rights, String menuIds);
}
