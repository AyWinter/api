package com.mash.api.service;

import com.mash.api.entity.Employee;

import java.util.List;

public interface EmployeeService {

    Employee save(Employee employee);

    List<Employee> findByDepartmentId(Integer departmentId);

    Employee findByMobileNo(String mobileNo);

    Employee findById(Integer id);

    void updateRights(Integer id, String rights, String menuIds);
}
