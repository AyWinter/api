package com.mash.api.controller.vendor;

import com.mash.api.entity.*;
import com.mash.api.service.*;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import com.mash.api.util.WebUtil;
import com.mash.api.util.WechatConfig;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@RestController
public class VendorRefundController {

    private static final Logger log = LoggerFactory.getLogger(VendorRefundController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private RefundService refundService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeService employeeService;

    /**
     * 获取用户申请的退款记录
     * @param request
     * @return
     */
    @GetMapping(value="/vendor/refund/list")
    public Result<Refund> findByVendorId(HttpServletRequest request)
    {
        Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);

        return ResultUtil.success(refundService.findByVendorId(vendorId));
    }

    @PostMapping(value="/vendor/refund")
    public Result refund(@RequestParam("orderNo")String orderNo,
                         @RequestParam("refundId")Integer refundId,
                         @RequestParam("remark")String remark,
                         HttpServletRequest request)
    {
        try
        {
            SortedMap<String, Object> map = new TreeMap<String, Object>();

            map.put("appid", WechatConfig.appid);
            map.put("mch_id", WechatConfig.mchid);
            map.put("nonce_str", Tools.getRandomString(32));
            map.put("out_trade_no", orderNo);
            // 退款单号
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String out_refund_no = Tools.FormatDateToString(sdf, d)
                    + Tools.getRandomString(4);

            // 退款单号
            map.put("out_refund_no", out_refund_no);
            // 退款金额 单位（分）
            MainOrder order = this.findOrderByOrderNo(orderNo);
            float orderTotal = order.getAmount();
            log.info("退款订单金额orderTotal = " + orderTotal);
            int orderTotalInt = (int)orderTotal * 100;
            log.info("退款金额：orderTotalInt（分） = " + orderTotalInt);
            orderTotalInt = 1;
            map.put("total_fee", orderTotalInt);
            map.put("refund_fee", orderTotalInt);

            String mySign = Tools.createSign(map);
            map.put("sign", mySign);

            // 退款签名
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            String path = WechatConfig.WECHAT_REFUND_SIGN_CERT;
            String shh = WechatConfig.mchid;
            FileInputStream instream = new FileInputStream(new File(path));
            try {
                keyStore.load(instream, shh.toCharArray());
            } finally {
                instream.close();
            }
            // Trust own CA and all self-signed certs
            SSLContext sslcontext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, shh.toCharArray()).build();
            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[] { "TLSv1" },
                    null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf).build();

            HttpPost httppost = new HttpPost(WechatConfig.REFUND_URL);
            StringEntity se = new StringEntity(WebUtil.mapToXml(map));
            httppost.setEntity(se);

            CloseableHttpResponse responseEntry = httpClient.execute(httppost);

            HttpEntity entity = responseEntry.getEntity();
            log.info("退款请求结果：entity = " + entity);
            Map<String, Object> resultMap = WebUtil.entityToMap(entity);
            log.info("退款请求结果：resultMap = " + resultMap);
            if (resultMap.get("return_code").equals("SUCCESS")) {
                if (resultMap.get("result_code").equals("SUCCESS")) {

                    // 操作人
                    Integer vendorId = Tools.getVendorLoginUserId(request);
                    String vendorMobileNo = accountService.findById(vendorId).getMobileNo();
                    orderService.updateRefundState(orderNo, out_refund_no, new Date(), orderTotal, refundId, vendorMobileNo, remark);

                    return ResultUtil.success();
                }
                else
                {
                    return ResultUtil.fail(-1, "退款申请失败");
                }
            }
            else
            {
                return ResultUtil.fail(-1, "退款申请失败");
            }
        }
        catch(Exception e){
            return ResultUtil.fail(-1, "退款申请失败");
        }
    }

    /**
     * 获取订单信息
     * @param orderNo
     * @return
     */
    public MainOrder findOrderByOrderNo(String orderNo)
    {
        MainOrder order = orderService.findByOrderNo(orderNo);

        return order;
    }
}
