package com.mash.api.controller;

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

@RestController
public class BidJoinUserController {

    @Autowired
    private BidJoinUserService bidJoinUserService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProductService productService;

    @PostMapping(value="/bidJoinUser")
    public Result<BidJoinUser> save(@RequestParam("mobileNo") String mobileNo,
                                    @RequestParam("productId") Integer productId)
    {
        Account account = accountService.findByMobileNo(mobileNo);

        if (account == null)
        {
            return ResultUtil.fail(-1, "用户不存在，请核对手机号码");
        }

        Integer accountId = account.getId();

        BidJoinUser bidJoinUser = new BidJoinUser();
        bidJoinUser.setAccountId(accountId);
        // 获取广告位信息
        Product product = productService.findById(productId);
        bidJoinUser.setProduct(product);

        return ResultUtil.success(bidJoinUserService.save(bidJoinUser));
    }

    /**
     * 判断用户是否有权限参与此竞价
     * @param productId
     * @param request
     * @return
     */
    @GetMapping(value="/bidJoinUser/{productId}")
    public Result findByProductId(@PathVariable("productId")Integer productId,
                                               HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);

        // 获取产品信息
        Product product = productService.findById(productId);
        Integer joinMode = product.getBidParameter().getJoinMode();
        if (joinMode == 0)
        {
            return ResultUtil.success();
        }
        else
        {
            BidJoinUser bidJoinUser = bidJoinUserService.findByAccountIdAndProductId(accountId, productId);
            if (bidJoinUser == null)
            {
                return ResultUtil.fail(-1,"暂时无权限参与此广告位竞价");
            }
            else
            {
                return ResultUtil.success();
            }
        }
    }
}
