package com.mash.api.repository;

import com.mash.api.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer>{

    List<Employee> findByDepartmentId(Integer departmentId);

    Employee findByMobileNo(String mobileNo);

    @Transactional
    @Query(value="update employee set rights=?2, menu_ids=?3 where id=?1", nativeQuery = true)
    @Modifying
    void updateRights(Integer id, String rights, String menuIds);
}
