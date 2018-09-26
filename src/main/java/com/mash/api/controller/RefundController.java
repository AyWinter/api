package com.mash.api.controller;

import com.mash.api.entity.*;
import com.mash.api.service.*;
import com.mash.api.util.*;
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
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.web.bind.annotation.*;

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
public class RefundController {

    private static final Logger log = LoggerFactory.getLogger(RefundController.class);

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
     * 退款申请
     * @param productId
     * @param request
     * @return
     */
    @PostMapping("/refund/apply")
    public Result<Refund> refundApply(@RequestParam("productId")Integer productId,
                                      @RequestParam("orderNo")String orderNo,
                                      HttpServletRequest request)
    {
        Refund refund = refundService.refundApply(orderNo, productId, request);

        return ResultUtil.success(refund);
    }
}
