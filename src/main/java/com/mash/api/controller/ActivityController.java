package com.mash.api.controller;

import com.mash.api.entity.Activity;
import com.mash.api.entity.Position;
import com.mash.api.entity.Product;
import com.mash.api.entity.Result;
import com.mash.api.service.AccountService;
import com.mash.api.service.ActivityService;
import com.mash.api.service.PositionService;
import com.mash.api.service.ProductService;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ActivityController {


    @Autowired
    private ActivityService activityService;

    @Autowired
    private ProductService productService;

    @Autowired
    private PositionService positionService;

    @PostMapping(value="/activity")
    public Result<Activity> save(Activity activity,
                                 HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);
        activity.setAccountId(accountId);
        return ResultUtil.success(activityService.save(activity));
    }

    @GetMapping(value="/activity")
    public Result<Activity> findByAccountId(HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);
        return ResultUtil.success(activityService.findByAccountId(accountId));
    }

    @GetMapping(value="/activity/position/{positionId}")
    public Result<Activity> findActivityByProductId(@PathVariable("positionId")Integer positionId)
    {
        Position position = positionService.findById(positionId);

        Integer vendorId = position.getVendorId();

        return ResultUtil.success(activityService.findByAccountId2(vendorId));
    }

    @DeleteMapping(value="/activity/{id}")
    public Result deleteById(@PathVariable("id")Integer id)
    {
        activityService.deleteById(id);
        return ResultUtil.success();
    }
}
