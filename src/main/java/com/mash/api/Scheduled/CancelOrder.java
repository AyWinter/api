package com.mash.api.Scheduled;

import com.mash.api.controller.AccountController;
import com.mash.api.entity.MainOrder;
import com.mash.api.entity.Schedule;
import com.mash.api.service.OrderService;
import com.mash.api.service.ScheduleService;
import com.mash.api.util.Const;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.WebUtil;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class CancelOrder {

    private static final Logger log = LoggerFactory.getLogger(CancelOrder.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private ScheduleService scheduleService;

    @Scheduled(fixedRate = 1000)
    public void timerRate()
    {
        List<MainOrder> orderList = orderService.findNotCancel();

        for (MainOrder order : orderList)
        {
            // 验证是否已超过30分钟
            Date createdTime = order.getCreatedTime();
            Calendar createdCal = Calendar.getInstance();
            createdCal.setTime(createdTime);
            createdCal.add(Calendar.MINUTE, Const.PAY_TIME);

            Date nowTime = new Date();
            Date lastTime = createdCal.getTime();
            if (nowTime.before(lastTime))
            {
                return;
            }

            String orderNo = order.getOrderNo();
            if (orderNo == null)
            {
                return;
            }
            log.info("订单：" + orderNo + "支付时间超过30分钟，自动取消");
            // 如果该订单已在达美创建，则发送请求取消达美排期单
            String scheduleNumber = order.getSchedule().getNumber();
            if (order.getVendorId() == Const.DM_ACCOUNT_ID)
            {
//                Integer scheduleId = order.getSchedule().getId();
//                log.info("排期单ID：" + scheduleId);
//                Schedule schedule = scheduleService.findById(scheduleId);
//                String scheduleNumber = schedule.getNumber();
//                log.info("排期单号：" + scheduleNumber);
                JSONObject result = cancelSchedule(scheduleNumber);
                Integer code = Integer.valueOf(result.get("code").toString());
                if (code == 0)
                {
                    // 排期取消成功，更新本地数据
                    orderService.cancel(orderNo);
                    log.info("达美服务器排期单和本地排期单已取消，");
                }
                else
                {
                    String msg = result.getString("msg");
                }
            }
            else
            {
                // 直接更新本地数据
                orderService.cancel(orderNo);
                log.info("本地排期单已取消，");
            }
        }
    }

    /**
     * 取消排期单
     * @param scheduleNumber
     * @return
     */
    public JSONObject cancelSchedule(String scheduleNumber)
    {
        List<NameValuePair> paramsList = new ArrayList();

        paramsList.add(new BasicNameValuePair("key", Const.DM_KEY));
        paramsList.add(new BasicNameValuePair("usingNumber", scheduleNumber));

        JSONObject result = WebUtil.post2(Const.DM_API_URL_CANCEL_SCHEDULE, paramsList);

        return result;
    }
}
