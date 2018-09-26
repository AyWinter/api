package com.mash.api.controller;

import com.mash.api.entity.MainOrder;
import com.mash.api.entity.PictureLibrary;
import com.mash.api.entity.Result;
import com.mash.api.entity.Schedule;
import com.mash.api.service.OrderService;
import com.mash.api.service.PictureLibraryService;
import com.mash.api.service.ScheduleService;
import com.mash.api.util.Const;
import com.mash.api.util.OssUtil;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PictureLibrayController {

    private static final Logger log = LoggerFactory.getLogger(PictureLibrayController.class);

    @Autowired
    private PictureLibraryService pictureLibraryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 获取订单下所有画面
     * @param scheduleNumber
     * @return
     */
    @GetMapping(value="/order/picture/{scheduleNumber}")
    public Result findByOrderNo(@PathVariable("scheduleNumber")String scheduleNumber)
    {

        List<PictureLibrary> pictureLibraryList = pictureLibraryService.findByScheduleNumber(scheduleNumber);
        return ResultUtil.success(pictureLibraryList);
    }

    /**
     * 获取排期单号
     * @param orderNo
     * @return
     */
    @GetMapping(value="/order/schedule/{orderNo}")
    public Result schedule(@PathVariable("orderNo")String orderNo)
    {
        MainOrder order = orderService.findByOrderNo(orderNo);
        String scheduleNumber = order.getSchedule().getNumber();

        return ResultUtil.success(scheduleNumber);
    }

    /**
     * 保存画面
     * @param scheduleNumber
     * @param pictureBase64
     * @param vendorId
     * @return
     */
    @PostMapping("/schedule/picture/upload")
    public Result upload(@RequestParam("pictureBase64")String pictureBase64,
                        @RequestParam("scheduleNumber")String scheduleNumber,
                         @RequestParam("vendorId")Integer vendorId)
    {
        try
        {
            // 上传广告画面
            String picture = "picture_" + scheduleNumber + "_" +  Tools.randomFourDigit() + ".jpg";
            String picturePath = Tools.uploadImg(pictureBase64, picture);

            if (picturePath.equals("error"))
            {
                return ResultUtil.fail(-1,"画面上传失败");
            }
            // 获取客户信息
            Schedule schedule = scheduleService.findByNumber(scheduleNumber);
            String customerName = schedule.getProject().getCustomer().getName();
            PictureLibrary pictureLibrary = new PictureLibrary();
            pictureLibrary.setFilePath(picturePath);
            pictureLibrary.setCustomerName(customerName);
            pictureLibrary.setScheduleNumber(scheduleNumber);
            pictureLibrary.setVendorId(vendorId);
            pictureLibrary.setState(0);

            pictureLibraryService.save(pictureLibrary);

            return ResultUtil.success();
        }
        catch(Exception e)
        {
            log.error(e.getMessage());
            log.error(e.getLocalizedMessage());
            return ResultUtil.fail(-1, e.getMessage());
        }
    }

    /**
     * 删除画面
     * @param id
     * @return
     */
    @DeleteMapping(value="/schedule/picture/{id}")
    public Result delete(@PathVariable("id")Integer id)
    {
        PictureLibrary pictureLibrary = pictureLibraryService.findById(id);
        if (pictureLibrary != null)
        {
            String filePath = pictureLibrary.getFilePath();
            OssUtil.deleteFile(filePath);

            pictureLibraryService.deleteById(id);
        }
        return ResultUtil.success();
    }
}
