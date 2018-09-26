package com.mash.api.controller.vendor;

import com.mash.api.entity.Department;
import com.mash.api.entity.Employee;
import com.mash.api.entity.Enterprise;
import com.mash.api.entity.Result;
import com.mash.api.service.AccountService;
import com.mash.api.service.DepartmentService;
import com.mash.api.service.EmployeeService;
import com.mash.api.service.EnterpriseService;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class VendorEnterpriseController {

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeService employeeService;

    /**
     * 获取企业信息根据accountId
     * @param request
     * @return
     */
    @GetMapping(value = "/vendor/enterprise")
    public Result<Enterprise> findByAccountId(HttpServletRequest request)
    {
        Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);

        return ResultUtil.success(enterpriseService.getByAccountId(vendorId));
    }
}
