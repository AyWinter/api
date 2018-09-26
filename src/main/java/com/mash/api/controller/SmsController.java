package com.mash.api.controller;

import com.mash.api.entity.Account;
import com.mash.api.entity.Result;
import com.mash.api.service.AccountService;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.SmsUtil;
import com.mash.api.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class SmsController {

    private static final Logger log = LoggerFactory.getLogger(SmsController.class);

    @Autowired
    private AccountService accountService;

    @GetMapping(value="/sms/findPwd/{mobileNo}/{kaptcha}")
    public Result sendSmsFindPassword(@PathVariable("mobileNo")String mobileNo,
                          @PathVariable("kaptcha")String kaptcha,
                          HttpServletRequest request) throws Exception {
        // 验证图形验证码
        if (!Tools.validateKaptcha(request, kaptcha))
        {
            return ResultUtil.fail(-1, "图形验证码输入错误");
        }

        // 判断用户是否存在
        Account account = accountService.findByMobileNo(mobileNo);
        if (account == null)
        {
            return ResultUtil.fail(-1, "用户不存在");
        }

        // 验证码
        Integer verifyCode = Tools.randomFourDigit();
        String msg = "验证码：" + verifyCode + "，请妥善保存。此验证码仅用于找回密码。";
        log.info(msg);

        String result = SmsUtil.batchSend(msg, mobileNo);

        if (result.equals("0"))
        {
            // 验证码保存到session
            HttpSession session = request.getSession();
            session.setAttribute(mobileNo, verifyCode);
            return ResultUtil.success();
        }
        else
        {
            return ResultUtil.fail(-1,"发送失败");
        }
    }

    @GetMapping(value="/sms/regist/{mobileNo}/{kaptcha}")
    public Result sendSms(@PathVariable("mobileNo")String mobileNo,
                          @PathVariable("kaptcha")String kaptcha,
                          HttpServletRequest request) throws Exception {
        // 验证图形验证码
        if (!Tools.validateKaptcha(request, kaptcha))
        {
            return ResultUtil.fail(-1, "图形验证码输入错误");
        }
        // 验证码
        Integer verifyCode = Tools.randomFourDigit();
        String msg = "注册验证码：" + verifyCode + "，请妥善保存。";
        log.info(msg);

        String result = SmsUtil.batchSend(msg, mobileNo);

        if (result.equals("0"))
        {
            // 验证码保存到session
            HttpSession session = request.getSession();
            session.setAttribute(mobileNo, verifyCode);
            return ResultUtil.success();
        }
        else
        {
            return ResultUtil.fail(-1,"发送失败");
        }
    }
}
