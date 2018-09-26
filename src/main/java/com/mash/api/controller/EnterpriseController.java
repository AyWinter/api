package com.mash.api.controller;

import com.mash.api.annotation.Auth;
import com.mash.api.entity.Enterprise;
import com.mash.api.entity.Result;
import com.mash.api.service.EnterpriseService;
import com.mash.api.util.Const;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;

@RestController
public class EnterpriseController {

    private static final Logger log = LoggerFactory.getLogger(EnterpriseController.class);

    @Autowired
    private EnterpriseService enterpriseService;

    /**
     * 保存企业信息
     * @param enterprise
     * @param bindingResult
     * @param request
     * @param businessLicenseBase64
     * @param legalPersonIdcardBase64
     * @return
     */
    @PostMapping(value="/enterprise")
    public Result<Enterprise> save(@Valid Enterprise enterprise,
                                   BindingResult bindingResult,
                                   HttpServletRequest request,
                                   @RequestParam String businessLicenseBase64,
                                   @RequestParam String legalPersonIdcardBase64)
    {
        if (bindingResult.hasErrors()) {
            return ResultUtil.fail(-1, bindingResult.getFieldError().getDefaultMessage());
        }

        int accountId = Tools.getUserId(request);
        if (enterprise.getId() == 0)
        {
            log.info("添加企业信息start");
            if (Tools.isEmpty(businessLicenseBase64))
            {
                return ResultUtil.fail(-1, "请上传营业执照图片");
            }
            if (Tools.isEmpty(legalPersonIdcardBase64))
            {
                return ResultUtil.fail(-1, "请上传法人身份证图片");
            }

            // 营业执照图片上传
            String businessLicenseFileName = "business_license_" + accountId + ".jpg";
            String businessLicensePath = Tools.uploadImg(businessLicenseBase64, businessLicenseFileName);
            if (businessLicensePath.equals("error"))
            {
                return ResultUtil.fail(-1, "营业执照上传失败");
            }
            enterprise.setBusinessLicense(businessLicensePath);

            // 法人身份证图片上传
            String legalPersonIdcardFileName = "legel_person_" + accountId + ".jpg";
            String legalPersonIdcardPath = Tools.uploadImg(legalPersonIdcardBase64, legalPersonIdcardFileName);
            if (legalPersonIdcardPath.equals("error"))
            {
                return ResultUtil.fail(-1, "法人身份证图片上传失败");
            }
            enterprise.setLegalPersonIdcard(legalPersonIdcardPath);

            enterprise.setCreatedTime(new Date());
        }
        else
        {
            log.info("编辑企业信息");
            if (!Tools.isEmpty(businessLicenseBase64))
            {
                // 营业执照图片上传
                String businessLicenseFileName = "business_license_" + accountId + ".jpg";
                String businessLicensePath = Tools.uploadImg(businessLicenseBase64, businessLicenseFileName);
                if (businessLicensePath.equals("error"))
                {
                    return ResultUtil.fail(-1, "营业执照上传失败");
                }
                enterprise.setBusinessLicense(businessLicensePath);
            }
            if (!Tools.isEmpty(legalPersonIdcardBase64))
            {
                String legalPersonIdcardFileName = "legel_person_" + accountId + ".jpg";
                String legalPersonIdcardPath = Tools.uploadImg(legalPersonIdcardBase64, legalPersonIdcardFileName);
                if (legalPersonIdcardPath.equals("error"))
                {
                    return ResultUtil.fail(-1, "法人身份证图片上传失败");
                }
                enterprise.setLegalPersonIdcard(legalPersonIdcardPath);
            }
        }

        enterprise.setUpdatedTime(new Date());
        enterprise.setState(Const.EXAMINE_ING);
        enterprise.setAccountId(accountId);

        return ResultUtil.success(enterpriseService.save(enterprise));
    }

    /**
     * 获取企业信息根据accountId
     * @param request
     * @return
     */
    @GetMapping(value = "/enterprise")
    public Result<Enterprise> findByAccountId(HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);

        return ResultUtil.success(enterpriseService.getByAccountId(accountId));
    }

    @GetMapping(value="/enterprise/{accountId}")
    public Result<Enterprise> findByAccountId(@PathVariable("accountId")Integer accountId)
    {
        return ResultUtil.success(enterpriseService.getByAccountId(accountId));
    }
}
