package com.mash.api.controller.vendor;

import com.mash.api.entity.Account;
import com.mash.api.entity.Customer;
import com.mash.api.entity.Project;
import com.mash.api.entity.Result;
import com.mash.api.service.*;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sun.misc.Request;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class VendorProjectController {

    private static final Logger log = LoggerFactory.getLogger(VendorProjectController.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CustomerService customerService;

    /**
     * 查询项目
     * @param request
     * @param name  项目名称
     * @param number   项目编号
     * @param department    业务部门
     * @param employee    业务员
     * @param startTime     立项开始时间
     * @param endTime    立项结束时间
     * @param state    项目状态
     * @param tradeState     成单状态
     * @return
     */
    @PostMapping(value="/vendor/project/search")
    public Result<Project> findAll(HttpServletRequest request,
                                   @RequestParam("name")String name,
                                   @RequestParam("number")String number,
                                   @RequestParam("department")String department,
                                   @RequestParam("employee")String employee,
                                   @RequestParam("startTime")String startTime,
                                   @RequestParam("endTime")String endTime,
                                   @RequestParam("state")Integer state,
                                   @RequestParam("tradeState")Integer tradeState,
                                   @RequestParam("examineState")Integer examineState,
                                   @RequestParam("page")Integer page)
    {
        Pageable pageable = new PageRequest(page,10, Sort.Direction.DESC,"id");
        final String projectName = name;
        final String projectNumber = number;
        final String businessDepartment = department;
        final String businessEmployee = employee;
        final Date createdStartTime = startTime != "" ? Tools.str2Date(startTime + " 00:00:00") : null;
        final Date createdEndTime = endTime != "" ? Tools.str2Date(endTime + " 23:59:59") : null;;
        final Integer projectState = state;
        final Integer projectTradeState = tradeState;
        final Integer projectExamineState = examineState;

        // 获取vendorId
        final Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);

        Specification<Project> specification = new Specification<Project>(){

            @Override
            public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicate = new ArrayList<>();

                predicate.add(criteriaBuilder.equal(root.get("vendorId").as(Integer.class), vendorId));

                if (!Tools.isEmpty(projectName))
                {
                    predicate.add(criteriaBuilder.equal(root.get("name").as(String.class), projectName));
                }
                if (!Tools.isEmpty(projectNumber))
                {
                    predicate.add(criteriaBuilder.equal(root.get("number").as(String.class), projectNumber));
                }
                if (!Tools.isEmpty(businessDepartment))
                {
                    predicate.add(criteriaBuilder.equal(root.get("department").as(String.class), businessDepartment));
                }
                if (!Tools.isEmpty(businessEmployee))
                {
                    predicate.add(criteriaBuilder.equal(root.get("employee").as(String.class), businessEmployee));
                }
                if (createdStartTime != null)
                {
                    predicate.add(criteriaBuilder.greaterThan(root.get("createdTime").as(Date.class), createdStartTime));
                }
                if (createdEndTime != null)
                {
                    predicate.add(criteriaBuilder.lessThan(root.get("createdTime").as(Date.class), createdEndTime));
                }
                if (projectState != -1)
                {
                    predicate.add(criteriaBuilder.equal(root.get("state").as(Integer.class), projectState));
                }
                if (projectTradeState != -1)
                {
                    predicate.add(criteriaBuilder.equal(root.get("tradeState").as(Integer.class), projectTradeState));
                }
                if (projectExamineState != -1)
                {
                    predicate.add(criteriaBuilder.equal(root.get("examineState").as(Integer.class), projectExamineState));
                }

                Predicate[] pre = new Predicate[predicate.size()];
                return criteriaQuery.where(predicate.toArray(pre)).getRestriction();
            }
        };

        return ResultUtil.success(projectService.findAll(specification, pageable));
    }

    /**
     * 立项
     * @param project
     * @param bindingResult
     * @param request
     * @return
     */
    @PostMapping(value="/vendor/project")
    public Result<Project> save(@Valid Project project,
                                @RequestParam("customerId")Integer customerId,
                                BindingResult bindingResult,
                                HttpServletRequest request)
    {
        if (bindingResult.hasErrors())
        {
            return ResultUtil.fail(-1, bindingResult.getFieldError().getDefaultMessage());
        }

        // 获取客户信息
        Customer customer = customerService.findById(customerId);

        // 获取登录用户信息
        Integer accountId = Tools.getVendorLoginUserId(request);
        Account account = accountService.findById(accountId);
        // 获取企业信息
        Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);

        project.setOperator(account.getMobileNo());
        project.setVendorId(vendorId);
        project.setCreatedTime(new Date());
        // 流转状态
        project.setExamineState(0);
        project.setState(0);
        project.setCustomer(customer);

        return ResultUtil.success(projectService.save(project));
    }

    /**
     * 做成项目编号
     * @return
     */
    @GetMapping(value="/vendor/project/number")
    public Result makeProjectNumber()
    {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

        String number = "P" + Tools.FormatDateToString(sdf, date);

        return ResultUtil.success(number);
    }

    @GetMapping(value="/vendor/project/{id}")
    public Result<Project> findById(@PathVariable("id")Integer id)
    {
        return ResultUtil.success(projectService.findById(id));
    }

    @DeleteMapping(value="/vendor/project/{id}")
    public Result deleteById(@PathVariable("id")Integer id)
    {
        projectService.deleteById(id);
        return ResultUtil.success();
    }
}
