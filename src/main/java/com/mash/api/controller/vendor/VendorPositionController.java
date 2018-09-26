package com.mash.api.controller.vendor;

import com.mash.api.entity.Position;
import com.mash.api.entity.Product;
import com.mash.api.entity.Result;
import com.mash.api.service.*;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class VendorPositionController {

    private static final Logger log = LoggerFactory.getLogger(VendorPositionController.class);

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private PositionService positionService;

    /**
     * 站点保存
     * @param position
     * @param bindingResult
     * @param address
     * @param request
     * @return
     */
    @PostMapping(value="/vendor/position")
    public Result<Position> save(@Valid Position position,
                                 BindingResult bindingResult,
                                 @RequestParam("address")String address,
                                 HttpServletRequest request)
    {
        if (bindingResult.hasErrors())
        {
            return ResultUtil.fail(-1, bindingResult.getFieldError().getDefaultMessage());
        }

        // 获取vendorId
        Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);

        String[] locationArray = address.split(",");
        position.setProvince(locationArray[0].trim());
        position.setCity(locationArray[1].trim());
        position.setArea(locationArray[2].trim());
        position.setVendorId(vendorId);
        position = positionService.save(position);

        return ResultUtil.success(position);
    }

    /**
     * 站点查询
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @PostMapping(value="/vendor/position/all")
    public Result<Position> findAll(@RequestParam("page")Integer page,
                                    @RequestParam("pageSize")Integer pageSize,
                                    HttpServletRequest request)
    {
        // 获取vendorId
        final Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);

        Pageable pageable = new PageRequest(page, pageSize, Sort.Direction.DESC,"id");

        Specification<Position> specification = new Specification<Position>()
        {
            @Override
            public Predicate toPredicate(Root<Position> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicate = new ArrayList<>();

                predicate.add(criteriaBuilder.equal(root.get("vendorId").as(Integer.class), vendorId));

                Predicate[] pre = new Predicate[predicate.size()];
                return criteriaQuery.where(predicate.toArray(pre)).getRestriction();
            }
        };

        Page<Position> positionPage = positionService.findAll(specification, pageable);
        return ResultUtil.success(positionPage);
    }
}
