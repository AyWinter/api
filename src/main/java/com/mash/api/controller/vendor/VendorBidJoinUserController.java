package com.mash.api.controller.vendor;

import com.mash.api.entity.Account;
import com.mash.api.entity.BidJoinUser;
import com.mash.api.entity.Product;
import com.mash.api.entity.Result;
import com.mash.api.service.AccountService;
import com.mash.api.service.BidJoinUserService;
import com.mash.api.service.ProductService;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
public class VendorBidJoinUserController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProductService productService;

    @Autowired
    private BidJoinUserService bidJoinUserService;

    @PostMapping(value="/vendor/bidJoinUser")
    public Result<BidJoinUser> save(@RequestParam("mobileNo") String mobileNo,
                                    @RequestParam("remarks")String remarks,
                                    @RequestParam("productId") Integer productId,
                                    HttpServletRequest request)
    {
        Account account = accountService.findByMobileNo(mobileNo);

        if (account == null)
        {
            return ResultUtil.fail(-1, "用户不存在，请核对手机号码");
        }

        Integer accountId = account.getId();
        // 系统登录用户
        Integer loginId = Tools.getVendorLoginUserId(request);
        String operator = accountService.findById(loginId).getMobileNo();

        BidJoinUser bidJoinUser = new BidJoinUser();
        bidJoinUser.setAccountId(accountId);
        bidJoinUser.setMobileNo(mobileNo);
        bidJoinUser.setOperator(operator);
        bidJoinUser.setRemarks(remarks);
        bidJoinUser.setOperateTime(new Date());
        // 获取广告位信息
        Product product = productService.findById(productId);
        bidJoinUser.setProduct(product);

        return ResultUtil.success(bidJoinUserService.save(bidJoinUser));
    }

    @DeleteMapping(value="/vendor/bidJoinUser/{id}")
    public Result delete(@PathVariable("id")Integer id)
    {
        bidJoinUserService.deleteById(id);
        return ResultUtil.success();
    }
}
