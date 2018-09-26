package com.mash.api.service.impl;

import com.mash.api.entity.Department;
import com.mash.api.repository.DepartmentRepository;
import com.mash.api.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService{

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public List<Department> findByEnterpriseId(Integer enterpriseId) {
        return departmentRepository.findByEnterpriseId(enterpriseId);
    }

    @Override
    public Department findById(Integer id) {
        return departmentRepository.findOne(id);
    }

    @Override
    public void updateRights(Integer id, String rights, String menuIds) {
        departmentRepository.updateRights(id, rights, menuIds);
    }
}
