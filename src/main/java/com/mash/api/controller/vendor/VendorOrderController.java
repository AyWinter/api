package com.mash.api.controller.vendor;

import com.mash.api.entity.MainOrder;
import com.mash.api.entity.Result;
import com.mash.api.service.OrderService;
import com.mash.api.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@RestController
public class VendorOrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping(value="/vendor/order")
    public Result<MainOrder> findAll(@RequestParam("page")Integer page,
                                     @RequestParam("state")Integer state)
    {
        Pageable pageable = new PageRequest(page,10, Sort.Direction.DESC,"id");
        final Integer payState = state;

        Specification<MainOrder> specification = new Specification<MainOrder>()
        {

            @Override
            public Predicate toPredicate(Root<MainOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicate = new ArrayList<>();
                // 支付状态
                predicate.add(criteriaBuilder.equal(root.get("state").as(Integer.class), payState));
                Predicate[] pre = new Predicate[predicate.size()];
                return criteriaQuery.where(predicate.toArray(pre)).getRestriction();
            }
        };
        return ResultUtil.success(orderService.findAll(specification, pageable));
    }

    @GetMapping(value="/vendor/order/{orderNo}")
    public Result<MainOrder> findByOrderNo(@PathVariable("orderNo")String orderNo)
    {
        return ResultUtil.success(orderService.findByOrderNo(orderNo));
    }
}
