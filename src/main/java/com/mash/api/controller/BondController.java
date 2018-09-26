package com.mash.api.controller;

import com.mash.api.entity.*;
import com.mash.api.service.BidResultService;
import com.mash.api.service.BondService;
import com.mash.api.service.OrderService;
import com.mash.api.service.ProductService;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sun.applet.Main;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
public class BondController {

    @Autowired
    private BondService bondService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BidResultService bidResultService;

    @Autowired
    private ProductService productService;

    @GetMapping(value="/bond/{productId}")
    public Result getBond(@PathVariable("productId")Integer productId, HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);
        // 获取保证金信息
        Bond bond = bondService.findByAccountIdAndProductId(accountId, productId);
        // 判断是否支付
        MainOrder order = orderService.findById(bond.getOrder().getId());

        if (order.getState() == 1)
        {
            // 支付成功
            return ResultUtil.success();
        }
        else
        {
            return ResultUtil.fail(-2, "未支付保证金");
        }
    }

    @GetMapping(value="/bond/user")
    public Result<Bond> findByAccountId(HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);

        List<MainOrder> bonds = orderService.findRefundBondOrder(accountId);

        return ResultUtil.success(bonds);
    }

    /**
     * 判断是否可以申请退保证金
     * @param productId
     * @param request
     * @return
     */
    @GetMapping(value="/bond/refund/check/{productId}")
    public Result checkRefund(@PathVariable("productId")Integer productId,
                              HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);

        BidResult bidResult = bidResultService.findByProductIdAndAccountId(productId, accountId);
        if (bidResult == null)
        {
            // 判断竞价产品是否已经到期
            Product product = productService.findById(productId);
            Date endTime = product.getBidParameter().getEndTime();
            // 当前时间
            Date currentTime = new Date();
            if (currentTime.before(endTime))
            {
                return ResultUtil.fail(-1, "该广告位正在竞价中，请竞价结束后申请退保证金");
            }
            else
            {
                return ResultUtil.success();
            }
        }
        else
        {
            Integer state = bidResult.getState();
            if (state == 0)
            {
                return ResultUtil.fail(-1, "您已拍得该广告位，请下单付款后在申请退保证金");
            }
            else if (state == 1)
            {
                return ResultUtil.fail(-1, "您已拍得该广告位，请在【用户中心】【待付款】中付款后在申请退保证金");
            }
            else if (state == 2)
            {
                return ResultUtil.success();
            }
            else
            {
                return ResultUtil.success();
            }
        }
    }
}
