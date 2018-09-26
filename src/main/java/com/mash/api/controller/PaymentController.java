package com.mash.api.controller;

import com.mash.api.entity.*;
import com.mash.api.repository.OrderProductRepository;
import com.mash.api.repository.ProductPeriodRepository;
import com.mash.api.service.OrderService;
import com.mash.api.util.*;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private ProductPeriodRepository productPeriodRepository;

    /**
     * 获取微信支付签名
     * @param request
     * @return
     */
    @GetMapping(value = "/wechatPaySign/{orderNo}")
    public Result WechatPaySign(HttpServletRequest request,
                                @PathVariable("orderNo")String orderNo) {
        log.info("get wechatpay sign start");

        // 获取订单信息
        MainOrder order = orderService.findByOrderNo(orderNo);
        if (order == null)
        {
            log.info("订单：{}无效", orderNo);
            return ResultUtil.fail(-1, "无效订单");
        }
        else
        {
            Integer state = order.getState();
            if (state == 1)
            {
                log.info("订单：{}已支付。", orderNo);
                return ResultUtil.fail(-1, "订单已支付.");
            }
        }

        // openId
        Cookie[] cookies = request.getCookies();
        Cookie openIdCookie = null;
        for (Cookie cookie : cookies) {
            if ("openId".equals(cookie.getName())) {
                openIdCookie = cookie;
                break;
            }
        }

        if (openIdCookie == null)
        {
            return ResultUtil.fail(-1, "登录信息已过期，请关闭公众号重新登录");
        }

        String value = openIdCookie.getValue();

        String openId = RedisUtil.getStr(value).toString();
        log.info("openId = {}", openId);

        // 订单开始时间
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time_start = Tools.FormatDateToString(sdf, d);
        // 订单结束时间
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 5);
        String time_expire = Tools.FormatDateToString(sdf, c.getTime());

        SortedMap<String, Object> map = new TreeMap<String, Object>();

        map.put("appid", WechatConfig.appid);
        map.put("mch_id", WechatConfig.mchid);
        map.put("device_info", "WEB");
        // 随机字符串
        map.put("nonce_str", Tools.getRandomString(32));
        map.put("detail", "湖南城广");
        // 订单类型
        int orderType = order.getType();
        if (orderType == 1)
        {
            map.put("body", "广告位购买");
        }
        else if (orderType == 2)
        {
            map.put("body", "竞价保证金");
        }
        else
        {
            map.put("body", "");
        }
        map.put("attach", "");
        long seconds = System.currentTimeMillis() / 1000;
        // 交易订单号
        map.put("out_trade_no", orderNo);
        map.put("fee_type", "CNY");
        // 订单金额
        log.info("订单金额：{}" + order.getAmount());
        log.info("折扣金额：{}" + order.getDiscountAmount());
        float orderAmount = order.getAmount() - order.getDiscountAmount();
        log.info("实际结算金额：{}" + orderAmount);
        Integer amount = (int) (orderAmount * 100);
        log.info("order total amount：{}" + amount);
        map.put("total_fee", "1");
        map.put("spbill_create_ip", "192.169.1.1");
        // 订单开始时间
        map.put("time_start", time_start);
        // 订单结束时间
        map.put("time_expire", time_expire);
        map.put("goods_tag", "WXG");
        map.put("notify_url", WechatConfig.wechat_notify_url);
        // 支付方式
        map.put("trade_type", "JSAPI");
        map.put("product_id", "123");
        // 禁止无卡支付
        map.put("limit_pay", "no_credit");
        map.put("openid", openId);

        String mySign = Tools.createSign(map);

        // 签名
        map.put("sign", mySign);

        // 发送请求
        String resXml = WebUtil.doPost(WechatConfig.place_order_url, map);

        log.info("Unify a single return to the result ：" + resXml);
        String prepay_id = WebUtil.getWechatprepayId(resXml);

        SortedMap<String, Object> signMap = new TreeMap<String, Object>();

        String nonceStr = Tools.getRandomString(32);
        signMap.put("appId", WechatConfig.appid);
        signMap.put("timeStamp", Long.toString(seconds));
        signMap.put("nonceStr", nonceStr);
        signMap.put("package", "prepay_id=" + prepay_id);
        signMap.put("signType", "MD5");

        String sign = Tools.createSign(signMap);

        String signType = "MD5";
        WechatPayParams wechatPayParams = new WechatPayParams();
        wechatPayParams.setSign(sign);
        wechatPayParams.setWechatPackage("prepay_id=" + prepay_id);
        wechatPayParams.setAppId(WechatConfig.appid);
        wechatPayParams.setTimeStamp(Long.toString(seconds) + "");
        wechatPayParams.setNonceStr(nonceStr);
        wechatPayParams.setSignType(signType);

        return ResultUtil.success(wechatPayParams);
    }

    /**
     * 微信支付后台回调通知
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/wechatpay/notify")
    public void wechatNotify(HttpServletRequest request,
                               HttpServletResponse response) {
        SortedMap<String, Object> resultMap = WebUtil.parseXml(request);
        String return_code = resultMap.get("return_code").toString();
        log.info("return_code = {}", return_code);
        Map<String, Object> handleResult = new HashMap<String, Object>();

        if (return_code.equalsIgnoreCase("SUCCESS")) {
            // 返回签名
            String returnSign = resultMap.get("sign").toString();

            log.info("wechat return sign：" + returnSign);

            String mySign = Tools.createSign(resultMap);
            log.info("make sign：" + mySign);
            if (!mySign.equals(returnSign)) {
                handleResult.put("return_code", "FAIL");
                log.info("sign fail");
                try {
                    response.getWriter()
                            .write(WebUtil.mapToXml(handleResult));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                log.info("notify success");

                // 订单号
                String orderNo = resultMap.get("out_trade_no").toString();
                // 获取订单信息
                MainOrder order = orderService.findByOrderNo(orderNo);
                if (order != null)
                {
                    int state = order.getState();
                    if (state == 1)
                    {
                        log.info("订单已支付。");
                    }
                    else
                    {
                        log.info("更新订单状态start");
                        // 支付成功
                        state = 1;
                        // 支付方式：1 微信支付
                        Integer payMethod = 1;
                        // 支付时间
                        Date payTime = new Date();
                        // 微信支付订单号 （申请退款时候用）
                        String transactionId = resultMap.get("transaction_id").toString();
                        // 订单类型
                        Integer orderType = order.getType();
                        Integer orderId = order.getId();

                        orderService.updateOrderState(state, payMethod, payTime, transactionId, orderNo, orderType, orderId);
                        log.info("订单：{}更新成功。", orderNo);

                        // 如果资源方是达美，则发送请求更新排期单状态
                        Integer vendorId = order.getVendorId();
                        String scheduleNumber = order.getSchedule().getNumber();
                        if (vendorId == Const.DM_ACCOUNT_ID)
                        {
                            JSONObject result = updateSchedule(scheduleNumber);
                            Integer code = Integer.valueOf(result.get("code").toString());
                            if (code == 0)
                            {
                                log.info("排期单:" + scheduleNumber + "签约成功");
                            }
                            else
                            {
                                String msg = result.getString("msg");
                                log.info("排期单:" + scheduleNumber + " 签约失败, 错误消息:" + msg);
                            }
                        }
                    }
                }
                else
                {
                    log.info("order：{}exception，order is empty。", orderNo);
                }

                try {
                    handleResult.put("return_code", "SUCCESS");
                    response.getWriter()
                            .write(WebUtil.mapToXml(handleResult));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @GetMapping(value="/order/check/{orderNo}")
    public Result orderCheck(@PathVariable("orderNo")String orderNo)
    {
        String result = payCheck(orderNo);
        if (!Tools.isEmpty(result))
        {
            return ResultUtil.fail(-1, result);
        }
        else
        {
            return ResultUtil.success();
        }
    }

    /**
     * 判断订单是否可以支付
     * @param orderNo
     * @return
     */
    public String payCheck(String orderNo)
    {
        MainOrder order = orderService.findByOrderNo(orderNo);
        Set<OrderProduct> orderProducts = order.getOrderProducts();

        for (OrderProduct orderProduct : orderProducts)
        {
            // 广告位期限
            Set<ProductPeriod> periods = orderProduct.getProduct().getPeriods();
            Iterator<ProductPeriod> it = periods.iterator();
            while(it.hasNext())
            {
                ProductPeriod productPeriod = it.next();
                if (productPeriod.getState() ==  0 ||
                        productPeriod.getState() ==  1 ||
                        productPeriod.getState() ==  2 ||
                        productPeriod.getState() ==  6 ||
                        productPeriod.getState() ==  7 )
                {
                    continue;
                }
                Date pStartTime = productPeriod.getStartTime();
                Date pEndTime = productPeriod.getEndTime();

                // 判断所选时间段是否已被占用
                boolean result = Tools.dateCheckOverlap(orderProduct.getStartTime(), orderProduct.getEndTime(), pStartTime, pEndTime);
                if (result)
                {
                    log.info("所选时间段被占用");

                    return orderProduct.getProduct().getName();
                }
            }
        }

        return "";
    }

    /**
     * 取消排期单
     * @param scheduleNumber
     * @return
     */
    public JSONObject updateSchedule(String scheduleNumber)
    {
        List<NameValuePair> paramsList = new ArrayList();

        paramsList.add(new BasicNameValuePair("key", Const.DM_KEY));
        paramsList.add(new BasicNameValuePair("usingNumber", scheduleNumber));

        JSONObject result = WebUtil.post2(Const.DM_API_URL_SIGN_SCHEDULE, paramsList);

        return result;
    }

}
