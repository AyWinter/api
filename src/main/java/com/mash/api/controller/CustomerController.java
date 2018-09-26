package com.mash.api.controller;

import com.mash.api.entity.Customer;
import com.mash.api.entity.Result;
import com.mash.api.service.CustomerService;
import com.mash.api.util.Const;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import com.mash.api.util.WebUtil;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @GetMapping(value="/customer")
    public Result findByAccountId(HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);

        return ResultUtil.success(customerService.findByAccountId(accountId));
    }

    /**
     * 保存客户信息
     * @param id
     * @param name
     * @param phone
     * @param invoiceTitle
     * @param taxIdentificationNumber
     * @param depositBank
     * @param depositNumber
     * @param address
     * @param request
     * @return
     */
    @PostMapping(value="/customer")
    public Result save(@RequestParam("id")Integer id,
                       @RequestParam("vendorId")Integer vendorId,
                       @RequestParam("name")String name,
                       @RequestParam("phone")String phone,
                       @RequestParam("invoiceTitle")String invoiceTitle,
                       @RequestParam("taxIdentificationNumber")String taxIdentificationNumber,
                       @RequestParam("depositBank")String depositBank,
                       @RequestParam("depositNumber")String depositNumber,
                       @RequestParam("address")String address,
                       HttpServletRequest request)
    {
        Customer customer = null;
        if (id == 0)
        {
            customer = new Customer();
            Integer accountId = Tools.getUserId(request);
            customer.setAccountId(accountId);
            customer.setNumber(Tools.makeCustomerNumber());
        }
        else
        {
            customer = customerService.findById(id);
        }

        // 如果是达美，则保存用户信息到达美系统
        if (vendorId == Const.DM_ACCOUNT_ID)
        {
            // 判断是否已达美注册
            JSONObject client = getCustomerByDM(name);
            // 如果客户信息不存在，则保存
            if (Integer.valueOf(client.get("code").toString()) == 1)
            {
                log.info("客户信息不存在，发送请求保存到达美服务器");
                JSONObject result = saveToDM(name, phone);
                Integer code = Integer.valueOf(result.get("code").toString());
            }
            else if (Integer.valueOf(client.get("code").toString()) == 0)
            {
                log.info("客户已在达美注册");
                // 已注册
                JSONObject data = JSONObject.fromObject(client.get("data"));
                String clientNumber = data.get("clientNumber").toString();

                customer.setNumber(clientNumber);
            }
        }
        customer.setVendorId(vendorId);
        customer.setName(name);
        customer.setPhone(phone);
        customer.setAddress(address);
        customer.setInvoiceTitle(invoiceTitle);
        customer.setTaxIdentificationNumber(taxIdentificationNumber);
        customer.setDepositBank(depositBank);
        customer.setDepositNumber(depositNumber);
        customer.setAbbreviation(name);
        customer.setOperateTime(new Date());
        customer.setOperator("system");
        customerService.save(customer);
        return ResultUtil.success();
    }

    /**
     * 保存客户信息到达美系统
     * @param name
     * @param phoneNo
     * @return
     */
    public JSONObject saveToDM(String name, String phoneNo)
    {
        List<NameValuePair> paramsList = new ArrayList();

        paramsList.add(new BasicNameValuePair("key", Const.DM_KEY));
        paramsList.add(new BasicNameValuePair("clientName", name));
        paramsList.add(new BasicNameValuePair("companyPhone", phoneNo));
        JSONObject result = JSONObject.fromObject(WebUtil.post2(Const.DM_API_URL_ADD_CUSTOMER, paramsList));

        return result;
    }

    /**
     * 根据名称获取用户信息
     * @param name
     * @return
     */
    public JSONObject getCustomerByDM(String name)
    {
        List<NameValuePair> paramsList = new ArrayList();

        paramsList.add(new BasicNameValuePair("key", Const.DM_KEY));
        paramsList.add(new BasicNameValuePair("clientName", name));
        JSONObject result = WebUtil.post2(Const.DM_API_URL_GET_CUSTOMER_BY_NAME, paramsList);

        return result;
    }
}
