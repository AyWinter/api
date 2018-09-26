package com.mash.api.controller.vendor;

import com.mash.api.entity.Department;
import com.mash.api.entity.Enterprise;
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

@RestController
public class VendorDepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping(value="/vendor/enterrpise")
    public Result<Enterprise> enterprise(HttpServletRequest request)
    {
        Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);

        return ResultUtil.success(enterpriseService.getByAccountId(vendorId));
    }

    @GetMapping(value="/vendor/enterprise/department/{enterpriseId}")
    public Result<Department> findAll(@PathVariable("enterpriseId")Integer enterpriseId)
    {
        return ResultUtil.success(departmentService.findByEnterpriseId(enterpriseId));
    }

    @PostMapping(value="/vendor/enterprise/department")
    public Result<Department> save(Department department,
                                   @RequestParam("enterpriseId")Integer enterpriseId)
    {
        Enterprise enterprise = enterpriseService.getById(enterpriseId);
        department.setEnterprise(enterprise);
        return ResultUtil.success(departmentService.save(department));
    }

    @GetMapping(value="/vendor/department/{id}")
    public Result<Department> findByDepartmentId(@PathVariable("id")Integer id)
    {
        return ResultUtil.success(departmentService.findById(id));
    }
}
