package com.mash.api.controller;

import com.mash.api.entity.BidResult;
import com.mash.api.entity.Product;
import com.mash.api.entity.Result;
import com.mash.api.entity.Shopcart;
import com.mash.api.service.BidResultService;
import com.mash.api.service.ProductService;
import com.mash.api.service.ShopcartService;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class BidResultController {

    @Autowired
    private BidResultService bidResultService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ShopcartService shopcartService;

    @GetMapping(value="/bidResult")
    public Result<BidResult> findByAccountId(HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);

        List<BidResult> bidResults = bidResultService.findByAccountIdAndState(accountId, 0);

        return ResultUtil.success(bidResults);
    }

    @GetMapping(value="/bidResult/{productId}")
    public Result findByProductId(@PathVariable("productId")Integer productId)
    {
        return ResultUtil.success(bidResultService.findByProductId(productId));
    }

    @PostMapping(value="/bidResult/cancel")
    public Result<BidResult> cancel(@RequestParam("id")Integer id,
                                         @RequestParam("state")Integer state)
    {
        bidResultService.updateState(id, state);

        return ResultUtil.success();
    }

    @PostMapping(value="/bidResult/buy")
    public Result addShopcart(@RequestParam("productId")Integer productId,
                              HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);

        // 将购物车其他产品选中状态设置为false
        List<Shopcart> shopcarts = shopcartService.findByAccountId(accountId);
        for (Shopcart shopcart: shopcarts)
        {
            shopcartService.updateSelected(false, shopcart.getId());
        }

        BidResult bidResult = bidResultService.findByProductId(productId);

        Shopcart shopcart = new Shopcart();
        shopcart.setAccountId(accountId);
        shopcart.setSelected(true);
        shopcart.setProduct(bidResult.getProduct());
        shopcart.setCycle(bidResult.getProduct().getBidParameter().getPriceUnit());
        shopcart.setQuantity(1);
        shopcart.setDeliverTime(bidResult.getProduct().getBidParameter().getDeliverTime());
        shopcart.setEndTime(Tools.calculateProductEndTime(bidResult.getProduct().getBidParameter().getDeliverTime(), 1, bidResult.getProduct().getBidParameter().getPriceUnit()));

        shopcartService.save(shopcart);

        return ResultUtil.success();
    }

    @GetMapping(value="/bidResult/all")
    public Result<BidResult> findAll()
    {
        return ResultUtil.success(bidResultService.findAll());
    }
}
