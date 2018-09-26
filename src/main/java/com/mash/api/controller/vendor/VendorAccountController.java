package com.mash.api.controller.vendor;

import com.mash.api.entity.Account;
import com.mash.api.entity.Employee;
import com.mash.api.entity.Enterprise;
import com.mash.api.entity.Result;
import com.mash.api.service.AccountService;
import com.mash.api.service.EmployeeService;
import com.mash.api.service.EnterpriseService;
import com.mash.api.util.RedisUtil;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;

@RestController
public class VendorAccountController {

    private static final Logger log = LoggerFactory.getLogger(VendorAccountController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private EmployeeService employeeService;

    /**
     * 资源方系统登录
     * @param mobileNo
     * @param password
     * @return
     */
    @GetMapping(value="/vendor/login/{mobileNo}/{password}")
    public Result<Account> adminLogin(@PathVariable("mobileNo")String mobileNo,
                                      @PathVariable("password")String password,
                                      HttpServletResponse response)
    {
        Account account = accountService.findByMobileNoAndPassword(mobileNo, Tools.encryptStrByMD5(password));
        if (account == null)
        {
            return ResultUtil.fail(-1,"用户名密码错误");
        }
        else
        {
            Integer accountId = account.getId();
            Enterprise enterprise = enterpriseService.getByAccountId(accountId);

            if (enterprise != null && enterprise.getState() == 1)
            {
                String token = saverLoginToken(accountId, response);
                return ResultUtil.success(token);
            }
            else
            {
                // 判断用户是否是公司员工
                Employee employee = employeeService.findByMobileNo(mobileNo);
                if (employee != null)
                {
                    String token = saverLoginToken(accountId, response);
                    return ResultUtil.success(token);
                }
                return ResultUtil.fail(-1,"权限不足");
            }
        }
    }

    @GetMapping(value="/vendor/account/{mobileNo}")
    public Result<Account> findByMobileNo(@PathVariable("mobileNo")String mobileNo)
    {
        Account account = accountService.findByMobileNo(mobileNo);
        if (account == null)
        {
            return ResultUtil.fail(-1, "用户不存在");
        }
        return ResultUtil.success(account);
    }

    /**
     * 保存登录令牌（资源方）
     * @param id
     * @param response
     * @return
     */
    public String saverLoginToken(Integer id, HttpServletResponse response)
    {
        log.info("保存登录令牌start");
        Calendar c = Calendar.getInstance();
        String token = Tools.encryptStrByMD5(String.valueOf(id) + c.getTime());
        // 过期时间30分钟
        RedisUtil.setStr(token, String.valueOf(id), 1800);

        Cookie cookie = new Cookie("vendorLoginId", token);
        cookie.setMaxAge(1800);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);
        return token;
    }
}
