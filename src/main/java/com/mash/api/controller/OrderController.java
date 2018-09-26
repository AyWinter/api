package com.mash.api.controller;

import com.mash.api.annotation.Auth;
import com.mash.api.entity.*;
import com.mash.api.repository.OrderProductRepository;
import com.mash.api.repository.PictureLibraryRepository;
import com.mash.api.service.*;
import com.mash.api.util.Const;
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
import sun.applet.Main;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.RequestWrapper;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private ShopcartService shopcartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private BondService bondService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private BidResultService bidResultService;

    @Autowired
    private ProductPeriodService productPeriodService;

    /**
     * 订单做成
     * @param name
     * @param phone
     * @param clientNumber
     * @param message
     * @param vendorId
     * @param request
     * @return
     */
    @PostMapping(value="/order")
    public Result<MainOrder> save(@RequestParam("name")String name,
                                  @RequestParam("phone")String phone,
                                  @RequestParam("clientNumber")String clientNumber,
                                  @RequestParam("message")String message,
                                  @RequestParam("vendorId")Integer vendorId,
                                  HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);
        log.info("判断是否可以下单，所选时间段是否已经被占用");
        List<Shopcart> products = shopcartService.findByAccountIdAndSelected(accountId, true);

        products = this.discountCalculate(products);

        // 如果是达美，则发送请求到达美创建排期
        String serverScheduleNumber = "";
        if (vendorId == Const.DM_ACCOUNT_ID)
        {
            log.info("资源方是达美，发送请求到达美服务器创建排期单");
            JSONObject result = createSchedule(products, clientNumber);
            Integer code = Integer.valueOf(result.get("code").toString());
            if (code != 0)
            {
                String msg = result.get("msg").toString();
                log.info("达美服务器创建排期单失败，错误信息：" + msg);
                return ResultUtil.fail(-1, msg);
            }
            else
            {
                log.info("达美服务器创建排期单成功");
                serverScheduleNumber = result.getString("data");
            }
        }
        else
        {
            // 普通产品则手动校验排期时间
            String checkResult = checkCreateOrder(products);
            if (!Tools.isEmpty(checkResult))
            {
                String msg = "广告位：" + checkResult + " 所选时段已被占用";
                return ResultUtil.fail(-1, msg);
            }
        }

        MainOrder order = new MainOrder();
        // 订单基本信息
        order.setAccountId(accountId);
        order.setOrderNo(Tools.createOrderNo());
        order.setCreatedTime(new Date());
        order.setType(1);
        order.setState(0);
        order.setMessage(message);
        order.setVendorId(vendorId);

        // 计算订单总金额
        float totalAmount = 0;
        float totalDiscountAmount = 0;
        for (Shopcart shopcart : products)
        {
            // 判断是否是竞价广告位
            if (shopcart.getProduct().getPrices().size() > 0)
            {
                for (ProductPrice productPrice : shopcart.getProduct().getPrices())
                {
                    if (shopcart.getCycle().equals(productPrice.getCycle()))
                    {
                        float discount = shopcart.getDiscount();
                        float amount = shopcart.getQuantity() * productPrice.getPrice();
                        float discountAmount = 0;
                        if (discount > 0)
                        {
                            // amount = amount * discount;
                            discountAmount = amount - (amount * discount);
                        }
                        totalAmount += amount;
                        totalDiscountAmount += discountAmount;
                        break;
                    }
                }
            }
            else
            {
                BidResult bidResult = bidResultService.findByProductId(shopcart.getProduct().getId());
                float amount = 0;
                if (bidResult.getAmount() < bidResult.getProduct().getBidParameter().getReservePrice())
                {
                    amount = bidResult.getProduct().getBidParameter().getReservePrice();
                }
                else
                {
                    amount = bidResult.getAmount();
                }
                totalAmount += amount;
            }
        }

        order.setAmount(totalAmount);
        order.setDiscountAmount(totalDiscountAmount);

        order = orderService.save(order, name, phone, products, serverScheduleNumber);

        return ResultUtil.success(order);
    }

    /**
     * 竞价保证金订单做成
     * @param productId
     * @return
     */
    @PostMapping(value="/order/bond")
    public Result<MainOrder> bondSave(@RequestParam("productId") Integer productId, HttpServletRequest requset)
    {
        // 保证金信息
        Integer accountId = Tools.getUserId(requset);

        // 判断是否已经创建支付保证金订单
        Bond bond = bondService.findByAccountIdAndProductId(accountId, productId);
        if (bond != null)
        {
            Integer orderId = bond.getOrder().getId();
            MainOrder order = orderService.findById(orderId);
            return ResultUtil.success(order);
        }
        else
        {
            bond = new Bond();
        }

        bond.setAccountId(accountId);
        bond.setProductId(productId);

        // 获取产品信息
        Product product = productService.findById(productId);

        MainOrder order = new MainOrder();
        // 订单基本信息
        order.setAccountId(accountId);
        order.setOrderNo(Tools.createOrderNo());
        order.setCreatedTime(new Date());
        order.setType(2);
        order.setState(0);
        // 订单金额
        order.setAmount(product.getBidParameter().getBidPrice());
        // 无折扣
        order.setDiscountAmount(0);
        order.setMessage("用户：" + accountId + "参与产品"+productId+"竞价保证金");

        return ResultUtil.success(orderService.bondSave(order, bond));
    }

    @GetMapping(value="/order")
    public Result<MainOrder> findAll()
    {
        return ResultUtil.success(orderService.findAll());
    }

    /**
     * 根据订单号获取订单信息
     * @param orderNo
     * @return
     */
    @GetMapping(value="/order/{orderNo}")
    public Result<MainOrder> findByOrderNo(@PathVariable("orderNo")String orderNo)
    {
        return ResultUtil.success(orderService.findByOrderNo(orderNo));
    }

    /**
     * 用户中心查看自己所有订单
     * @param request
     * @return
     */
    @GetMapping(value="/order/user")
    public Result<MainOrder> findByAccountId(HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);
        Integer type = 1;

        List<MainOrder> orders = orderService.findByAccountIdAndType(accountId, type);
        for (MainOrder order : orders)
        {
            if (order.getState() == 0)
            {
                Date createdTime = order.getCreatedTime();
                Calendar cancelTime = Calendar.getInstance();
                cancelTime.setTime(createdTime);

                // 支付时间30分钟
                cancelTime.add(Calendar.MINUTE, Const.PAY_TIME);

                Date cancelDate = cancelTime.getTime();
                Date nowTime = new Date();
                long surplusTime =(cancelDate.getTime() - nowTime.getTime()) / 1000;
                order.setSurplusTime(surplusTime);
            }
        }
        return ResultUtil.success(orders);
    }

    @GetMapping(value="/order/user/{payState}")
    public Result<MainOrder> findByAccountIdByState(HttpServletRequest request,
                                             @PathVariable("payState")Integer payState)
    {
        Integer accountId = Tools.getUserId(request);
        Integer type = 1;
        List<MainOrder> orders = orderService.findByAccountIdAndTypeAndState(accountId, type, payState);
        for (MainOrder order : orders)
        {
            if (order.getState() == 0)
            {
                Date createdTime = order.getCreatedTime();
                Calendar cancelTime = Calendar.getInstance();
                cancelTime.setTime(createdTime);

                // 支付时间30分钟
                cancelTime.add(Calendar.MINUTE, Const.PAY_TIME);

                Date cancelDate = cancelTime.getTime();
                Date nowTime = new Date();
                long surplusTime =(cancelDate.getTime() - nowTime.getTime()) / 1000;
                order.setSurplusTime(surplusTime);
            }
        }
        return ResultUtil.success(orders);
    }

    @GetMapping(value="/order/product")
    public Result findOrderProduct(HttpServletRequest request)
    {
        Integer userId = Tools.getUserId(request);
        List<OrderProduct> orderProducts = orderProductRepository.findOrderProductByVendorId(userId);
        return ResultUtil.success(orderProducts);
    }

    @GetMapping(value="/order/product/{productId}")
    public Result<MainOrder> findByProductId(@PathVariable("productId")Integer productId)
    {
        OrderProduct orderProduct = orderProductRepository.findByProductId(productId);

        Integer orderId = orderProduct.getOrder().getId();

        MainOrder order = orderService.findById(orderId);

        return ResultUtil.success(order);
    }

    @GetMapping(value="/order/product/detail/{id}")
    public Result<MainOrder> findOrderProductDetail(@PathVariable("id") Integer id)
    {
        MainOrder order = orderService.findByOrderProductId(id);
        return ResultUtil.success(order);
    }

    /**
     * 判断是否可以下单
     * @param shopcarts
     * @return
     */
    public String checkCreateOrder(List<Shopcart> shopcarts)
    {
        for (Shopcart shopcart : shopcarts)
        {
            Date selectedEndTime = Tools.calculateProductEndTime(shopcart.getDeliverTime(), shopcart.getQuantity(), shopcart.getCycle());

            // 查询该产品的所有排期
            List<ProductPeriod> productPeriods = productPeriodService.findByProductId(shopcart.getProduct().getId());
            for (ProductPeriod productPeriod : productPeriods)
            {
                Date pStartTime = productPeriod.getStartTime();
                Date pEndTime = productPeriod.getEndTime();

                // 判断所选时间段是否已被占用
                boolean result = Tools.dateCheckOverlap(shopcart.getDeliverTime(), selectedEndTime, pStartTime, pEndTime);
                if (result)
                {
                    log.info("所选时间段被占用");

                    return shopcart.getProduct().getName();
                }
            }
        }
        return "";
    }

    public List<Shopcart> discountCalculate(List<Shopcart> shopcarts)
    {
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

        return shopcarts;
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

    /**
     * 发送请求到达美服务器创建排期
     * @param products
     * @param clientNumber
     * @return
     */
    public JSONObject createSchedule(List<Shopcart> products, String clientNumber)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("clientNumber", clientNumber);

        JSONArray points = new JSONArray();

        for (Shopcart product : products)
        {
            JSONObject point = new JSONObject();
            point.put("customNumber", product.getProduct().getNumber());
            point.put("number", 1);
            point.put("stime", sdf.format(product.getDeliverTime()));
            point.put("etime", sdf.format(product.getEndTime()));

            points.add(point);
        }

        jsonObject.put("point", points);
        jsonObject.put("remark", "城广");
        jsonObject.put("title", clientNumber + "下单");

        System.out.println(jsonObject.toString());

        List<NameValuePair> paramsList = new ArrayList();

        paramsList.add(new BasicNameValuePair("key", Const.DM_KEY));
        paramsList.add(new BasicNameValuePair("data", jsonObject.toString()));

        JSONObject result = WebUtil.post2(Const.DM_API_URL_CREATE_SCHEDULE, paramsList);

        return result;
    }

    /**
     * 关闭订单
     * @param orderNo
     * @return
     */
    @PostMapping(value="/order/cancel")
    public Result cancel(@RequestParam("orderNo")String orderNo)
    {
        MainOrder order = orderService.findByOrderNo(orderNo);

        // 如果该订单已在达美创建，则发送请求取消达美排期单
        if (order.getVendorId() == Const.DM_ACCOUNT_ID)
        {
            String scheduleNumber = order.getSchedule().getNumber();

            JSONObject result = cancelSchedule(scheduleNumber);
            Integer code = Integer.valueOf(result.get("code").toString());
            if (code == 0)
            {
                // 排期取消成功，更新本地数据
                orderService.cancel(orderNo);
                log.info("达美服务器排期单和本地排期单已取消（页面级），");
                return ResultUtil.success();
            }
            else
            {
                String msg = result.getString("msg");
                return ResultUtil.fail(-1, msg);
            }
        }
        else
        {
            // 直接更新本地数据
            if (order.getState() != 3)
            {
                orderService.cancel(orderNo);
                log.info("本地排期单已取消（页面级），");
            }
            return ResultUtil.success();
        }
    }

    @GetMapping(value="/order/test")
    public Result test()
    {
        List<MainOrder> orderList = orderService.findNotCancel();

        for (MainOrder order : orderList)
        {
            String scheduleNumber = order.getSchedule().getNumber();
            System.out.println(scheduleNumber);
        }

        return ResultUtil.success();
//        MainOrder order = orderService.findByOrderNo("3db5e91982d448f2bb6bb4aa2c93d691");
//        String scheduleNumber = order.getSchedule().getNumber();
//        System.out.println(scheduleNumber);
//        return ResultUtil.success(order);
    }

    /**
     * 退款申请
     * @param orderNo
     * @param reason
     * @return
     */
    @PostMapping(value="/order/refund/apply")
    public Result refundApply(@RequestParam("orderNo")String orderNo,
                              @RequestParam("reason")String reason)
    {
        MainOrder order = orderService.findByOrderNo(orderNo);
        // 支付状态
        Integer payState = order.getState();
        // 排期状态
        Integer scheduleState = order.getSchedule().getState();
        // 支付状态为【已支付】排期状态为【已锁定】才可申请退款
        if (payState == 1 && scheduleState == 3)
        {
            log.info("订单：" + orderNo + " 申请退款。");
            orderService.refundApply(reason, new Date(), orderNo);
            return ResultUtil.success();
        }
        else
        {
            log.info("订单：" + orderNo + " 不符合退款要求。");
            return ResultUtil.fail(-1, "该订单不符合申请退款要求。");
        }
    }
}
