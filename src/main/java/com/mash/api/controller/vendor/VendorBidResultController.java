package com.mash.api.controller.vendor;

import com.mash.api.entity.Result;
import com.mash.api.service.BidResultService;
import com.mash.api.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VendorBidResultController {

    @Autowired
    private BidResultService bidResultService;

    @GetMapping(value="/vendor/bidResult/{productId}")
    public Result findByProductId(@PathVariable("productId")Integer productId)
    {
        return ResultUtil.success(bidResultService.findByProductId(productId));
    }
}
