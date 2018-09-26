package com.mash.api.controller.vendor;

import com.mash.api.entity.Activity;
import com.mash.api.entity.Result;
import com.mash.api.service.*;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;

@RestController
public class VendorActivityController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private ActivityService activityService;

    @GetMapping(value="/vendor/activity/page/{page}")
    public Result<Activity> findAll(HttpServletRequest request,
                                    @PathVariable("page")Integer page)
    {
        Pageable pageable = new PageRequest(page,1, Sort.Direction.DESC,"id");

        // 获取vendorId
        Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);

        return ResultUtil.success(activityService.findByAccountId(pageable, vendorId));
    }

    @PostMapping(value="/vendor/activity")
    public Result<Activity> save(Activity activity,
                                 HttpServletRequest request)
    {
        if (activity.getStartTime().after(activity.getEndTime()))
        {
            return ResultUtil.fail(-1, "请输入正确的起止时间");
        }
        // 获取vendorId
        Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);

        activity.setAccountId(vendorId);

        return ResultUtil.success(activityService.save(activity));
    }

    @DeleteMapping(value="/vendor/activity/{id}")
    public Result<Activity> delete(@PathVariable("id")Integer id)
    {
        activityService.deleteById(id);
        return ResultUtil.success();
    }

    @GetMapping(value="/vendor/activity/{id}")
    public Result<Activity> findById(@PathVariable("id")Integer id)
    {
        return ResultUtil.success(activityService.findById(id));
    }
}
