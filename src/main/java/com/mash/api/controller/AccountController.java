package com.mash.api.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.mash.api.annotation.Auth;
import com.mash.api.entity.*;
import com.mash.api.repository.WechatUserInfoRepository;
import com.mash.api.service.AccountService;
import com.mash.api.service.DepartmentService;
import com.mash.api.service.EmployeeService;
import com.mash.api.service.EnterpriseService;
import com.mash.api.util.RedisUtil;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private WechatUserInfoRepository wechatUserInfoRepository;

    @PostMapping(value = "/account/test")
    public Result<Account> TestRegist(Account account)
    {
        Date nowDate = new Date();
        account.setCreatedDate(nowDate);
        account.setPassword(Tools.encryptStrByMD5(account.getPassword()));
        account.setState(1);
        // 更新mysql
        account = accountService.add(account);

        return ResultUtil.success(account);
    }

    /**
     * 注册
     * @param account
     * @param bindingResult
     * @param kaptcha
     * @param mobileVerifyCode
     * @param request
     * @return
     */
    @PostMapping(value = "/account")
    public Result<Account> register(@Valid Account account, BindingResult bindingResult,
                                    @RequestParam("kaptcha") String kaptcha,
                                    @RequestParam("mobileVerifyCode") String mobileVerifyCode,
                                    HttpServletRequest request) {
        // 验证图形验证码
        if (!Tools.validateKaptcha(request, kaptcha))
        {
            return ResultUtil.fail(-1, "图形验证码输入错误");
        }

        if (bindingResult.hasErrors()) {
            return ResultUtil.fail(-1, bindingResult.getFieldError().getDefaultMessage());
        }
        // 验证手机验证码
        String mobileNo = account.getMobileNo();
        HttpSession session = request.getSession();
        Object verifyCode = session.getAttribute(mobileNo);
        if (verifyCode == null || !verifyCode.toString().equals(mobileVerifyCode))
        {
            return ResultUtil.fail(-1, "手机验证码输入错误");
        }

        // 判断用户是否已经注册
        if (isExist(account.getMobileNo())) {
            log.info("用户：{}", account.getMobileNo() + "已注册");
            return ResultUtil.fail(-2, "用户已注册");
        }
        Date nowDate = new Date();
        account.setCreatedDate(nowDate);
        account.setPassword(Tools.encryptStrByMD5(account.getPassword()));

        // 更新mysql
        account = accountService.add(account);
        // 更新redis
        RedisUtil.setObject("account:" + account.getId(), account);
        // 清除session
        session.removeAttribute(mobileNo);

        return ResultUtil.success(account);
    }

    /**
     * 通过手机号码获取账户信息
     *
     * @param mobileNo
     * @return
     */
    @GetMapping(value = "/account/mobileNo/{mobileNo}")
    public Result<Account> findByMobileNo(@PathVariable("mobileNo") String mobileNo) {
        Account account = accountService.findByMobileNo(mobileNo);

        if (account != null) {
            return ResultUtil.success(account);
        } else {
            return ResultUtil.fail(-1, "用户不存在");
        }
    }

    /**
     * 登录
     *
     * @param mobileNo
     * @param password
     * @param response
     * @return
     */
    @GetMapping(value = "/account/{mobileNo}/{password}/{kaptchaCode}")
    public Result<Account> findByMobileNoAndPassword(@PathVariable("mobileNo") String mobileNo,
                                                     @PathVariable("password") String password,
                                                     @PathVariable("kaptchaCode") String kaptchaCode,
                                                     HttpServletResponse response,
                                                     HttpServletRequest request) {

        if (Tools.isEmpty(mobileNo)) {
            return ResultUtil.fail(-1, "请输入手机号");
        }
        if (Tools.isEmpty(password)) {
            return ResultUtil.fail(-1, "请输入密码");
        }
        if (Tools.isEmpty(kaptchaCode)) {
            return ResultUtil.fail(-1, "请输入图形验证码");
        }

        if (!Tools.validateKaptcha(request, kaptchaCode)) {
            return ResultUtil.fail(-1, "图形验证码输入错误");
        }

        Account account = accountService.findByMobileNoAndPassword(mobileNo, Tools.encryptStrByMD5(password));

        if (account != null) {
            // 保存登录令牌
            String token = saveLoginToken(account.getId(), response);
            // 用户和微信信息绑定
            String openId = Tools.getOpenId(request);
            log.info("openId:" + openId);
            WechatUserInfo wechatUserInfo = wechatUserInfoRepository.findByOpenId(openId);
            if (wechatUserInfo != null)
            {
                log.info("openId和account绑定");
                wechatUserInfo.setAccountId(account.getId());
                wechatUserInfoRepository.save(wechatUserInfo);
            }
            return ResultUtil.success(token);
        } else {
            return ResultUtil.fail(-1, "用户名密码错误");
        }
    }

    /**
     * 重置密码
     *
     * @param id
     * @param password
     * @return
     */
    @Auth
    @PostMapping(value = "/account/{id}")
    public Result<Account> resetPassword(@PathVariable("id") Integer id,
                                         @RequestParam("password") String password) {
        Account account = accountService.findById(id);

        if (account != null) {
            account.setPassword(Tools.encryptStrByMD5(password));
            // 更新数据库
            account = accountService.add(account);

            // 更新缓存数据库
            RedisUtil.setObject("account:" + id, account);
            return ResultUtil.success(account);
        } else {
            return ResultUtil.fail(-1, "用户不存在，无法重置密码");
        }
    }

    /**
     * 通过ID获取账户信息
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/account/{id}")
    public Result<Account> getAccountById(@PathVariable("id") Integer id) {
        Object object = RedisUtil.getObject("account:" + id);
        if (object == null) {
            object = accountService.findById(id);
        }
        return ResultUtil.success((Account) object);
    }

    /**
     * 查询所有账户
     *
     * @return
     */
    @GetMapping(value = "/account")
    public Result<Account> getAll() {
        List<Account> accounts = accountService.findAll();

        return ResultUtil.success(accounts);
    }

    @DeleteMapping(value = "/account/{id}")
    public Result delete(@PathVariable("id") Integer id) {
        accountService.deleteById(id);
        return ResultUtil.success();
    }

    /**
     * 忘记密码
     * @param account
     * @param bindingResult
     * @return
     */
    @PostMapping("/forgetPassword")
    public Result<Account> forgetPassword(@Valid Account account,
                                          BindingResult bindingResult,
                                          @RequestParam String kaptcha,
                                          HttpServletRequest request)
    {
        if (Tools.validateKaptcha(request, kaptcha))
        {
            return ResultUtil.fail(-1, "图形验证码输入错误");
        }

        if (bindingResult.hasErrors()) {
            return ResultUtil.fail(-1, bindingResult.getFieldError().getDefaultMessage());
        }

        Account dbAccount = accountService.findByMobileNo(account.getMobileNo());
        if (dbAccount == null)
        {
            return ResultUtil.fail(-1, "用户不存在");
        }

        dbAccount.setPassword(Tools.encryptStrByMD5(account.getPassword()));

        return ResultUtil.success(accountService.add(dbAccount));
    }

    /**
     * 重置密码
     * @param mobileNo
     * @param password
     * @param confirmPassword
     * @return
     */
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestParam("mobileNo")String mobileNo,
                                @RequestParam("password")String password,
                                @RequestParam("confirmPassword")String confirmPassword)
    {
        if (!password.equals(confirmPassword))
        {
            return ResultUtil.fail(-1, "两次密码不一致");
        }

        Account account = accountService.findByMobileNo(mobileNo);
        String encryptPassword = Tools.encryptStrByMD5(password);

        account.setPassword(encryptPassword);

        account = accountService.resetPassword(account);

        return ResultUtil.success();
    }

    /**
     * 找回密码
     * @param mobileNo
     * @param mobileCode
     * @return
     */
    @GetMapping("/findPassword/verify/{mobileNo}/{mobileCode}")
    public Result findPasswordVerify(@PathVariable("mobileNo")String mobileNo,
                                     @PathVariable("mobileCode")String mobileCode,
                                     HttpServletRequest request)
    {
        if (Tools.isEmpty(mobileNo))
        {
            return ResultUtil.fail(-1, "请输入手机号码");
        }
        if (Tools.isEmpty(mobileCode))
        {
            return ResultUtil.fail(-1, "请输入手机验证码");
        }

        // 手机验证码校验
        HttpSession session = request.getSession();
        Object code = session.getAttribute(mobileNo);
        if (code == null || !mobileCode.equals(code.toString()))
        {
            return ResultUtil.fail(-1, "手机验证码输入错误");
        }
        return ResultUtil.success();
    }

    /**
     * 保存登录令牌
     *
     * @param id
     * @param response
     */
    public String saveLoginToken(Integer id, HttpServletResponse response) {
        log.info("保存登录令牌start");
        Calendar c = Calendar.getInstance();
        String token = Tools.encryptStrByMD5(String.valueOf(id) + c.getTime());
        // 过期时间30分钟
        RedisUtil.setStr(token, String.valueOf(id), 3600*8);

        Cookie cookie = new Cookie("loginid", token);
        cookie.setMaxAge(3600*8);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);
        log.info("保存登录令牌end");
        return token;
    }

    /**
     * 判断用户是否存在
     *
     * @param mobileNo
     * @return
     */
    public boolean isExist(String mobileNo) {
        Account account = accountService.findByMobileNo(mobileNo);

        if (account != null) {
            return true;
        }
        return false;
    }

    /**
     * 查看我的执行单 登录
     * @param mobileNo
     * @param password
     * @return
     */
    @GetMapping(value="/account/execute/{mobileNo}/{password}")
    public Result executeLogin(@PathVariable("mobileNo")String mobileNo,
                               @PathVariable("password")String password)
    {
        Account account = accountService.findByMobileNoAndPassword(mobileNo, Tools.encryptStrByMD5(password));

        if (account == null)
        {
            return ResultUtil.fail(-1,"用户名密码错误");
        }
        else
        {
            return ResultUtil.success(account);
        }
    }

    /**
     * 登出
     * @return
     */
    @GetMapping(value="/account/logout")
    public Result logout(HttpServletResponse response,
                         HttpServletRequest request)
    {
        log.info("登出");
        // openId 和 账户解绑
        String openId = Tools.getOpenId(request);
        WechatUserInfo wechatUserInfo = wechatUserInfoRepository.findByOpenId(openId);
        wechatUserInfo.setAccountId(null);
        wechatUserInfoRepository.save(wechatUserInfo);
        log.info("openId和用户ID已解绑");
        // 删除cookie
        Tools.removeLoginToken(response);
        log.info("登录cookie已删除");

        return ResultUtil.success();
    }
}
