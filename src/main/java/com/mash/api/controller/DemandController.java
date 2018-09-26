package com.mash.api.controller;

import com.mash.api.entity.Account;
import com.mash.api.entity.Demand;
import com.mash.api.entity.Result;
import com.mash.api.service.AccountService;
import com.mash.api.service.DemandService;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
public class DemandController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private DemandService demandService;

    @PostMapping(value="/demand")
    public Result<Demand> save(Demand demand, HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);
        Account account = accountService.findById(accountId);

        String mobileNo = account.getMobileNo();

        demand.setAccountId(accountId);
        demand.setMobileNo(mobileNo);
        demand.setPublishTime(new Date());

        return ResultUtil.success(demandService.save(demand));
    }

    @GetMapping(value="/demand")
    public Result<Demand> findAll()
    {
        return ResultUtil.success(demandService.findAll());
    }

    @GetMapping(value="/demand/acocunt")
    public Result<Demand> findAllByAccountId(HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);

        return ResultUtil.success(demandService.findByAccountId(accountId));
    }

    @GetMapping(value="/demand/{id}")
    public Result<Demand> detail(@PathVariable("id")Integer id)
    {
        return ResultUtil.success(demandService.findById(id));
    }
}
