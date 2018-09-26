package com.mash.api.controller;

import com.mash.api.entity.Result;
import com.mash.api.entity.VipMember;
import com.mash.api.service.VipMemberService;
import com.mash.api.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VipMemberController {

    @Autowired
    private VipMemberService vipMemberService;

    @PostMapping(value="/vipmember")
    public Result<VipMember> save(VipMember vipMember)
    {
        return ResultUtil.success(vipMemberService.save(vipMember));
    }
}
