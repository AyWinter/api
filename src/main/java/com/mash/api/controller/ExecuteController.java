package com.mash.api.controller;

import com.mash.api.entity.Result;
import com.mash.api.repository.ExecuteRepository;
import com.mash.api.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExecuteController {

    @Autowired
    private ExecuteRepository executeRepository;

    /**
     * 查询我的执行单
     * @return
     */
    @GetMapping("/execute/me/{accountId}")
    public Result findByAccountId(@PathVariable("accountId")Integer accountId)
    {
        return ResultUtil.success(executeRepository.findByWorkerAccountId(accountId));
    }
}
