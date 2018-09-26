package com.mash.api.controller;

import com.mash.api.entity.Account;
import com.mash.api.entity.BidRecord;
import com.mash.api.entity.Product;
import com.mash.api.entity.Result;
import com.mash.api.service.AccountService;
import com.mash.api.service.BidRecordService;
import com.mash.api.service.ProductService;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
public class BidRecordController {

    @Autowired
    private ProductService productService;

    @Autowired
    private BidRecordService bidRecordService;

    @Autowired
    private AccountService accountService;

    /**
     * 保存出价记录
     * @param productId
     * @param amount
     * @param request
     * @return
     */
    @PostMapping(value="/bidRecord")
    public Result<BidRecord> save(@RequestParam("productId")Integer productId,
                                  @RequestParam("amount")float amount,
                                  HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);

        // 获取用户信息
        Account account = accountService.findById(accountId);

        // 获取产品信息
        Product product = productService.findById(productId);

        BidRecord bidRecord = new BidRecord();
        bidRecord.setAccountId(accountId);
        bidRecord.setMobileNo(account.getMobileNo());
        bidRecord.setAmount(amount);
        bidRecord.setCreatedTime(new Date());
        bidRecord.setState(0);
        bidRecord.setProduct(product);

        return ResultUtil.success(bidRecordService.save(bidRecord));
    }

    @GetMapping(value="/bidRecord/highestPrice/{productId}")
    public Result<BidRecord> findHighestPrice(@PathVariable("productId")Integer productId)
    {
        BidRecord bidRecord = bidRecordService.findHighestPrice(productId);
        if (bidRecord == null)
        {
            // 获取产品信息
            Product product = productService.findById(productId);
            bidRecord = new BidRecord();
            bidRecord.setAmount(product.getBidParameter().getStartPrice());
        }
        return ResultUtil.success(bidRecord);
    }

    @GetMapping(value="/bidRecord/{productId}")
    public Result<BidRecord> findByProductId(@PathVariable("productId")Integer productId)
    {
        return ResultUtil.success(bidRecordService.findByProductId(productId));
    }

}
