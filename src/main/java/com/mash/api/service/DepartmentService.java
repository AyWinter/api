package com.mash.api.service;

import com.mash.api.entity.Department;

import java.util.List;

public interface DepartmentService {

    Department save(Department department);

    List<Department> findByEnterpriseId(Integer enterpriseId);

    Department findById(Integer id);

    void updateRights(Integer id, String rights, String menuIds);
}
