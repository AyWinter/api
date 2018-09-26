package com.mash.api.controller.vendor;

import com.mash.api.entity.Result;
import com.mash.api.entity.Schedule;
import com.mash.api.service.ScheduleService;
import com.mash.api.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 排期单 controller
 */
@RestController
public class VendorScheduleController {

    @Autowired
    private ScheduleService scheduleService;


    @GetMapping(value="/vendor/schedule")
    public Result<Schedule> findAll()
    {
        List<Schedule> schedules = scheduleService.findAll();

        return ResultUtil.success(schedules);
    }
}
