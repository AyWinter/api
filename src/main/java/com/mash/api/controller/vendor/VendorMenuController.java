package com.mash.api.controller.vendor;

import com.mash.api.entity.*;
import com.mash.api.repository.MenuRepository;
import com.mash.api.service.AccountService;
import com.mash.api.service.DepartmentService;
import com.mash.api.service.EmployeeService;
import com.mash.api.service.EnterpriseService;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.RightsUtil;
import com.mash.api.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class VendorMenuController {

    private static final Logger log = LoggerFactory.getLogger(VendorMenuController.class);

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EnterpriseService enterpriseService;

    @GetMapping(value="/vendor/menu")
    public Result<Menu> findAll(HttpServletRequest request)
    {
        // 所有菜单
        List<Menu> parentMenus = menuRepository.findByParentIdOrderByOrderIndex(0);

        for (Menu menu : parentMenus)
        {
            Integer id = menu.getId();
            // 子菜单
            List<Menu> children = menuRepository.findByParentIdOrderByOrderIndex(id);

            menu.setChildren(children);
        }

        Integer accountId = Tools.getVendorLoginUserId(request);

        // 如果企业用户 则拥有所有权限
        Enterprise enterprise = enterpriseService.getByAccountId(accountId);
        if (enterprise != null)
        {
            return ResultUtil.success(parentMenus);
        }
        // 企业员工拥有指定权限
        Account account = accountService.findById(accountId);
        String mobileNo = account.getMobileNo();

        log.info("accountId = {}", accountId);

        // 获取用户权限
        Employee employee = employeeService.findByMobileNo(mobileNo);
        String roleRights = employee.getRights();
        log.info("rights = {}", roleRights);
        // 如果个人权限为空则获取所在部门权限
        if (Tools.isEmpty(roleRights))
        {
            Integer departmentId = employee.getDepartment().getId();
            Department department = departmentService.findById(departmentId);

            roleRights = department.getRights();
        }

        if (!Tools.isEmpty(roleRights)) {

            for (Menu menu : parentMenus) {
                menu.setHasMenu(RightsUtil.testRights(roleRights,
                        menu.getId()));
                if (menu.isHasMenu()) {
                    List<Menu> subRightsList = menu.getChildren();
                    for (Menu sub : subRightsList) {
                        sub.setHasMenu(RightsUtil.testRights(roleRights,
                                sub.getId()));
                    }
                }
            }
        }

        return ResultUtil.success(parentMenus);
    }
}
