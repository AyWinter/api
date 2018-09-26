package com.mash.api.controller;

import com.mash.api.entity.Result;
import com.mash.api.entity.WechatUserInfo;
import com.mash.api.repository.WechatUserInfoRepository;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
public class UserInfoController {

    @Autowired
    private WechatUserInfoRepository wechatUserInfoRepository;

    @GetMapping(value="/userinfo")
    public Result<WechatUserInfo> findWechatUserInfo(HttpServletRequest request)
    {
        String openId = Tools.getOpenId(request);

        if (Tools.isEmpty(openId))
        {
            return ResultUtil.fail(-1,"登录用户已过期，请重新登录");
        }

        return ResultUtil.success(wechatUserInfoRepository.findByOpenId(openId));
    }
}
