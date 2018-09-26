package com.mash.api.service.impl;

import com.mash.api.entity.Employee;
import com.mash.api.repository.EmployeeRepository;
import com.mash.api.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> findByDepartmentId(Integer departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }

    @Override
    public Employee findByMobileNo(String mobileNo) {
        return employeeRepository.findByMobileNo(mobileNo);
    }

    @Override
    public Employee findById(Integer id) {
        return employeeRepository.findOne(id);
    }

    @Override
    public void updateRights(Integer id, String rights, String menuIds) {
        employeeRepository.updateRights(id, rights, menuIds);
    }
}
