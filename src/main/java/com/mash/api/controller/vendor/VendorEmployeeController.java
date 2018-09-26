package com.mash.api.controller.vendor;

import com.mash.api.entity.Department;
import com.mash.api.entity.Employee;
import com.mash.api.entity.Result;
import com.mash.api.service.AccountService;
import com.mash.api.service.DepartmentService;
import com.mash.api.service.EmployeeService;
import com.mash.api.service.EnterpriseService;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
public class VendorEmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EnterpriseService enterpriseService;

    @GetMapping(value="/vendor/enterprise/employee/{departmentId}")
    public Result<Employee> findByDepartmentId(@PathVariable("departmentId")Integer departmentId)
    {
        return ResultUtil.success(employeeService.findByDepartmentId(departmentId));
    }

    @GetMapping(value="/vendor/employee/{id}")
    public Result<Employee> findById(@PathVariable("id")Integer id)
    {
        return ResultUtil.success(employeeService.findById(id));
    }

    @PostMapping(value="/vendor/enterprise/employee")
    public Result<Employee> save(Employee employee,
                                 @RequestParam("departmentId")Integer departmentId,
                                 HttpServletRequest request)
    {
        Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);
        String operator = accountService.findById(vendorId).getMobileNo();

        Department department = departmentService.findById(departmentId);
        employee.setDepartment(department);
        employee.setCreatedTime(new Date());
        employee.setOperator(operator);
        return ResultUtil.success(employeeService.save(employee));
    }
}
