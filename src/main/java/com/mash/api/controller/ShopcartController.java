package com.mash.api.controller;

import com.mash.api.annotation.Auth;
import com.mash.api.entity.*;
import com.mash.api.service.ActivityService;
import com.mash.api.service.ProductPeriodService;
import com.mash.api.service.ProductService;
import com.mash.api.service.ShopcartService;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import com.mash.api.util.WebUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ShopcartController {

    private static final Logger log = LoggerFactory.getLogger(ShopcartController.class);

    @Autowired
    private ShopcartService shopcartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ProductPeriodService productPeriodService;

    @GetMapping(value="/shopcart")
    public Result<Shopcart> findByAccountId(HttpServletRequest request)
    {
        int accountId = Tools.getUserId(request);

        List<Shopcart> shopcarts = shopcartService.findByAccountId(accountId);

        for (int i = 0; i < shopcarts.size(); i ++)
        {
            Set<ProductPrice> productPrices = shopcarts.get(i).getProduct().getPrices();
            Iterator<ProductPrice> it = productPrices.iterator();
            float totalAmount = 0;
            while(it.hasNext())
            {
                ProductPrice productPrice = it.next();

                if (shopcarts.get(i).getCycle().equals(productPrice.getCycle()))
                {
                    totalAmount = productPrice.getPrice() * shopcarts.get(i).getQuantity();
                    break;
                }
            }
            List<Activity> activities = activityService.findByAccountId2(shopcarts.get(i).getProduct().getAccountId());

            Collections.sort(activities, new Comparator<Activity>() {
                @Override
                public int compare(Activity o1, Activity o2) {
                    if (o1.getAmount() < o2.getAmount())
                    {
                        return 1;
                    }
                    return -1;
                }
            });

            if (activities.size() == 1)
            {
                shopcarts.get(i).setAmount(activities.get(0).getAmount());
                shopcarts.get(i).setDiscount(activities.get(0).getDiscount());
            }
            else
            {
                for (int j = 0; j < activities.size(); j ++)
                {
                    if (totalAmount >= activities.get(j).getAmount())
                    {
                        shopcarts.get(i).setAmount(activities.get(j).getAmount());
                        shopcarts.get(i).setDiscount(activities.get(j).getDiscount());
                        break;
                    }
                    else
                    {
                        if (j < activities.size() - 1)
                        {
                            if (totalAmount < activities.get(j).getAmount() && totalAmount >= activities.get(j + 1).getAmount())
                            {
                                shopcarts.get(i).setAmount(activities.get(j + 1).getAmount());
                                shopcarts.get(i).setDiscount(activities.get(j + 1).getDiscount());
                                break;
                            }
                        }
                        else
                        {
                            if (totalAmount >= activities.get(j).getAmount())
                            {
                                shopcarts.get(i).setAmount(activities.get(j).getAmount());
                                shopcarts.get(i).setDiscount(activities.get(j).getDiscount());
                                break;
                            }
                        }
                    }
                }
            }
        }

        return ResultUtil.success(shopcarts);
    }

    @GetMapping(value="/shopcart/{selected}")
    public Result<Shopcart> findByAccountIdAndSelected(@PathVariable("selected")boolean selected,
                                                       HttpServletRequest request)
    {
        int accountId = Tools.getUserId(request);
        List<Shopcart> shopcarts = shopcartService.findByAccountIdAndSelected(accountId, selected);

        for (int i = 0; i < shopcarts.size(); i ++)
        {
            Set<ProductPrice> productPrices = shopcarts.get(i).getProduct().getPrices();
            Iterator<ProductPrice> it = productPrices.iterator();
            float totalAmount = 0;
            while(it.hasNext())
            {
                ProductPrice productPrice = it.next();

                if (shopcarts.get(i).getCycle().equals(productPrice.getCycle()))
                {
                    totalAmount = productPrice.getPrice() * shopcarts.get(i).getQuantity();
                    break;
                }
            }
            List<Activity> activities = activityService.findByAccountId2(shopcarts.get(i).getProduct().getAccountId());

            Collections.sort(activities, new Comparator<Activity>() {
                @Override
                public int compare(Activity o1, Activity o2) {
                    if (o1.getAmount() < o2.getAmount())
                    {
                        return 1;
                    }
                    return -1;
                }
            });

            if (activities.size() == 1)
            {
                shopcarts.get(i).setAmount(activities.get(0).getAmount());
                shopcarts.get(i).setDiscount(activities.get(0).getDiscount());
            }
            else
            {
                for (int j = 0; j < activities.size(); j ++)
                {
                    if (totalAmount >= activities.get(j).getAmount())
                    {
                        shopcarts.get(i).setAmount(activities.get(j).getAmount());
                        shopcarts.get(i).setDiscount(activities.get(j).getDiscount());
                        break;
                    }
                    else
                    {
                        if (j < activities.size() - 1)
                        {
                            if (totalAmount < activities.get(j).getAmount() && totalAmount >= activities.get(j + 1).getAmount())
                            {
                                shopcarts.get(i).setAmount(activities.get(j + 1).getAmount());
                                shopcarts.get(i).setDiscount(activities.get(j + 1).getDiscount());
                                break;
                            }
                        }
                        else
                        {
                            if (totalAmount >= activities.get(j).getAmount())
                            {
                                shopcarts.get(i).setAmount(activities.get(j).getAmount());
                                shopcarts.get(i).setDiscount(activities.get(j).getDiscount());
                                break;
                            }
                        }
                    }
                }
            }
        }

        return ResultUtil.success(shopcarts);
    }

    @PostMapping(value="/shopcart")
    public Result<Shopcart> save(Shopcart shopcart,
                                 Integer productId,
                                 HttpServletRequest request)
    {
        log.info("验证是否可以加入购物车");

        // 验证投放开始时间是否在现在时间以前
        Date startTime = shopcart.getDeliverTime();
        Date endTime = shopcart.getEndTime();
        // 当前时间
        Date currentTime = new Date();
        currentTime = Tools.formatDate(currentTime);
        if (startTime.compareTo(currentTime) < 0 || endTime.compareTo(startTime) < 0)
        {
            return ResultUtil.fail(-1, "请选择正确的时间");
        }

        // 验证所选时间段是否符合规定时间段
        if (!checkSelectedPeriod(startTime, endTime))
        {
            return ResultUtil.fail(-1, "请选择正确投放时间段");
        }

        boolean checkResult = checkCreateOrder(productId, startTime, endTime, shopcart.getCycle());
        if (!checkResult)
        {
            return ResultUtil.fail(-1, "所选时段已被占用");
        }
        Integer accountId = Tools.getUserId(request);
        shopcart.setAccountId(accountId);
        shopcart.setSelected(true);
        long quantity = Tools.differDate(startTime, endTime);
        shopcart.setQuantity((int) quantity);
        // shopcart.setEndTime(Tools.calculateProductEndTime(shopcart.getDeliverTime(), shopcart.getQuantity(), shopcart.getCycle()));
        Product product = productService.findById(productId);
        shopcart.setProduct(product);

        return ResultUtil.success(shopcartService.save(shopcart));
    }

    /**
     * 验证所选时间段是否符合规则
     * @param startTime
     * @param endTime
     * @return
     */
    public boolean checkSelectedPeriod(Date startTime, Date endTime){
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            List<String> periods = Tools.getBuyPeriod();

            // 第一哥时间段
            Date startTime1 = sdf.parse(periods.get(0).split("至")[0]);
            Date endTime1 = sdf.parse(periods.get(0).split("至")[1]);
            // 第二哥时间段
            Date startTime2 = sdf.parse(periods.get(1).split("至")[0]);
            Date endTime2 = sdf.parse(periods.get(1).split("至")[1]);
            // 第三哥时间段
            Date startTime3 = sdf.parse(periods.get(2).split("至")[0]);
            Date endTime3 = sdf.parse(periods.get(2).split("至")[1]);
            // 第四哥时间段
            Date startTime4 = sdf.parse(periods.get(3).split("至")[0]);
            Date endTime4 = sdf.parse(periods.get(3).split("至")[1]);

            // 如果所选时间段在第一个时间段范围内 则天数是否大约3
            if (startTime.compareTo(startTime1) >= 0 && endTime.compareTo(endTime1) <= 0)
            {
                long days = Tools.differDate(startTime, endTime);
                if (days < 3)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            // 所选开始日期在第一个时间段内，结束日期在第一个时间段结束日期以后，
            else if (startTime.compareTo(startTime1) >= 0 && startTime.compareTo(endTime1) <= 0 && endTime.compareTo(endTime1) > 0)
            {
                if (endTime.compareTo(endTime2) == 0 || endTime.compareTo(endTime3) == 0 || endTime.compareTo(endTime4) == 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else if (startTime.compareTo(endTime1) > 0)
            {
                if ((startTime.compareTo(startTime2) == 0 || startTime.compareTo(startTime3) == 0 || startTime.compareTo(startTime4) == 0)
                        && (endTime.compareTo(endTime2) == 0 || endTime.compareTo(endTime3) == 0 || endTime.compareTo(endTime4) == 0))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }

            return false;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public List<String> calculateTimePeriod()
    {
        Calendar calendar = Calendar.getInstance();

        Integer day = calendar.get(Calendar.DATE);
        return null;
    }

    @DeleteMapping(value="/shopcart/{id}")
    public Result delete(@PathVariable("id") Integer id)
    {
        shopcartService.delete(id);
        return ResultUtil.success();
    }

    @DeleteMapping(value="/shopcart")
    public Result deleteByAccountId(HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);
        shopcartService.deleteByAccountId(accountId);
        return ResultUtil.success();
    }

    @PostMapping(value="/shopcart/{id}")
    public Result<Shopcart> updateSelected(@PathVariable("id")Integer id,
                                           @RequestParam boolean selected)
    {
        shopcartService.updateSelected(selected, id);
        return ResultUtil.success();
    }

    @DeleteMapping(value="/shopcart/product/{productId}")
    public Result deleteByAccountIdAndProductId(@PathVariable("productId")Integer productId,
                                                HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);
        shopcartService.deleteByAccountIdAndProductId(accountId, productId);
        return ResultUtil.success();
    }

    /**
     * 判断是否可以下单或加入购物车
     * @param productId
     * @param selectedStartTime
     * @param selectedEndTime
     * @param cycle
     * @return
     */
    public boolean checkCreateOrder(Integer productId, Date selectedStartTime, Date selectedEndTime, String cycle)
    {
        // 获取产品信息
        Product product = productService.findById(productId);
        Integer pointId = product.getPointId();
        if (pointId == null || pointId == 0)
        {
            log.info("普通广告位，直接本地校验时间段是否重复");
            // Date selectedEndTime = Tools.calculateProductEndTime(selectedStartTime, quantity, cycle);

            // 查询该产品的所有排期
            List<ProductPeriod> productPeriods = productPeriodService.findByProductId(product.getId());
            for (ProductPeriod productPeriod : productPeriods)
            {
                Date pStartTime = productPeriod.getStartTime();
                Date pEndTime = productPeriod.getEndTime();

                // 判断所选时间段是否已被占用
                boolean result = Tools.dateCheckOverlap(selectedStartTime, selectedEndTime, pStartTime, pEndTime);
                if (result)
                {
                    log.info("所选时间段被占用");

                    return false;
                }
            }

            return true;
        }
        else
        {
            log.info("达美广告位，发送请求到达美服务器校验。");
            // 从达美服务端获取占用时间段
            String pointIdStr = pointId + "";
            String url = "http://dm.msplat.cn/api/point/unavaiTime.htm";
            List<NameValuePair> paramsList = new ArrayList();
            paramsList.add(new BasicNameValuePair("key", "112B41FA2DF34815983FC5DD82831EFC"));
            paramsList.add(new BasicNameValuePair("id", pointIdStr));
            // 获取点位被占用时间段
            JSONObject jsonObject = WebUtil.post2(url, paramsList);
            JSONArray data = (JSONArray)jsonObject.get("data");

            for (Integer i = 0; i < data.size(); i ++)
            {
                String period = data.get(i).toString();
                String startTimeStr = period.split("~")[0];
                String endTimeStr = period.split("~")[1];

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                Date startTime = Tools.str2Date(startTimeStr, sdf);
                Date endTime = Tools.str2Date(endTimeStr, sdf);

                // 判断所选时间段是否已被占用
                boolean result = Tools.dateCheckOverlap(selectedStartTime, selectedEndTime, startTime, endTime);
                if (result)
                {
                    log.info("所选时间段被占用");

                    return false;
                }
            }

            return true;
        }
    }
}
