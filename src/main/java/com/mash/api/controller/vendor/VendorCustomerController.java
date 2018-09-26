package com.mash.api.controller.vendor;

import com.mash.api.entity.Account;
import com.mash.api.entity.Customer;
import com.mash.api.entity.Employee;
import com.mash.api.entity.Result;
import com.mash.api.service.*;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
public class VendorCustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeService employeeService;

    /**
     * 查询企业内所有客户
     * @param request
     * @return
     */
    @GetMapping(value="/vendor/customer/{pageNo}/{pageSize}")
    public Result<Customer> findByVendorId(HttpServletRequest request,
                                           @PathVariable("pageNo")Integer pageNo,
                                           @PathVariable("pageSize")Integer pageSize)
    {
        Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);

        Pageable pageable = new PageRequest(pageNo,pageSize, Sort.Direction.DESC,"id");
        return ResultUtil.success(customerService.findByVendorId(pageable, vendorId));
    }

    /**
     * 查询企业内所有客户
     * @param request
     * @return
     */
    @GetMapping(value="/vendor/employee/customer/{employeeId}/{pageNo}")
    public Result<Customer> findByVendorIdAndEmployeeId(HttpServletRequest request,
                                           @PathVariable("employeeId")Integer employeeId,
                                           @PathVariable("pageNo")Integer pageNo)
    {
        Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);

        Pageable pageable = new PageRequest(pageNo,10, Sort.Direction.DESC,"id");
        return ResultUtil.success(customerService.findByVendorIdAndEmployeeId(pageable, vendorId, employeeId));
    }

    /**
     * 保存客户信息
     * @param customer
     * @param customerManagerId
     * @param request
     * @return
     */
    @PostMapping(value="/vendor/customer")
    public Result<Customer> save(@Valid Customer customer,
                                 BindingResult bindingResult,
                                 @RequestParam("customerManagerId")Integer customerManagerId,
                                 HttpServletRequest request)
    {
        if (bindingResult.hasErrors())
        {
            return ResultUtil.fail(-1, bindingResult.getFieldError().getDefaultMessage());
        }

        // 获取客户经理信息
        Employee employee = employeeService.findById(customerManagerId);

        // 获取登录用户信息
        Integer accountId = Tools.getVendorLoginUserId(request);
        Account account = accountService.findById(accountId);
        // 获取企业信息
        Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);

        customer.setVendorId(vendorId);
        customer.setEmployee(employee);
        customer.setOperateTime(new Date());
        customer.setOperator(account.getMobileNo());

        return ResultUtil.success(customerService.save(customer));
    }

    /**
     * 客户详细信息
     * @param id
     * @return
     */
    @GetMapping(value="/vendor/customer/detail/{id}")
    public Result<Customer> detail(@PathVariable("id")Integer id)
    {
        return ResultUtil.success(customerService.findById(id));
    }

    /**
     * 删除客户信息
     * @param id
     * @return
     */
    @DeleteMapping(value="/vendor/customer/{id}")
    public Result delete(@PathVariable("id")Integer id)
    {
        customerService.deleteById(id);
        return ResultUtil.success();
    }

    /**
     * 条件查询
     * @param name
     * @param number
     * @param page
     * @return
     */
    @PostMapping(value="/vendor/customer/search")
    public Result<Customer> search(@RequestParam("name")String name,
                                   @RequestParam("abbreviation")String abbreviation,
                                   @RequestParam("number")String number,
                                   @RequestParam("customerManagerId")Integer customerManagerId,
                                   @RequestParam("page")Integer page)
    {
        Pageable pageable = new PageRequest(page,10, Sort.Direction.DESC,"id");

        final String customerName = name;
        final String customerAbbreviation = abbreviation;
        final String customerNumber = number;
        final Integer finalCustomerManagerId = customerManagerId;
        Specification<Customer> specification = new Specification<Customer>(){

            @Override
            public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicate = new ArrayList<>();

                if (!Tools.isEmpty(customerName))
                {
                    predicate.add(criteriaBuilder.equal(root.get("name").as(String.class), customerName));
                }
                if (!Tools.isEmpty(customerAbbreviation))
                {
                    predicate.add(criteriaBuilder.equal(root.get("abbreviation").as(String.class), customerAbbreviation));
                }
                if (!Tools.isEmpty(customerNumber))
                {
                    predicate.add(criteriaBuilder.equal(root.get("number").as(String.class), customerNumber));
                }
                if (finalCustomerManagerId != 0)
                {
                    predicate.add(criteriaBuilder.equal(root.get("employee").get("id").as(Integer.class), finalCustomerManagerId));
                }

                Predicate[] pre = new Predicate[predicate.size()];
                return criteriaQuery.where(predicate.toArray(pre)).getRestriction();
            }
        };

        return ResultUtil.success(customerService.findByParams(specification, pageable));
    }

    /**
     * 做成客户编号
     * @return
     */
    @GetMapping(value="/vendor/customer/number")
    public Result makeCustomerNumber()
    {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

        String number = "KH" + Tools.FormatDateToString(sdf, date);

        return ResultUtil.success(number);
    }
}
