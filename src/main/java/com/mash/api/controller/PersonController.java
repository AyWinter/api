package com.mash.api.controller;

import com.mash.api.annotation.Auth;
import com.mash.api.entity.Person;
import com.mash.api.entity.Result;
import com.mash.api.exception.MyException;
import com.mash.api.service.PersonService;
import com.mash.api.util.OssUtil;
import com.mash.api.util.RedisUtil;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;

@RestController
public class PersonController {

    private static final Logger log = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    private PersonService personService;

    /**
     * 保存个人信息
     * @param idcardFrontBase64 身份证正面
     * @param idcardBackBase64  身份证反面
     * @param person
     * @param bindingResult
     * @return
     */
    @PostMapping(value="/person")
    public Result<Person> save(@RequestParam("idcardFrontBase64")String idcardFrontBase64,
                               @RequestParam("idcardBackBase64")String idcardBackBase64,
                               @Valid Person person,
                               BindingResult bindingResult,
                               HttpServletRequest request)
    {
        if (bindingResult.hasErrors()) {
            return ResultUtil.fail(-1, bindingResult.getFieldError().getDefaultMessage());
        }

        Integer userId = Tools.getUserId(request);
        if (person.getId() == 0)
        {
            log.info("添加实名信息start");
            if (Tools.isEmpty(idcardFrontBase64))
            {
                return ResultUtil.fail(-1, "请上传身份证正面图片");
            }

            if (Tools.isEmpty(idcardBackBase64))
            {
                return ResultUtil.fail(-1, "请上传身份证反面图片");
            }
            // 上传身份证正面图片
            String idcardFrontName = "idcard_front_" + userId + ".jpg";
            String idcardFrontPath = Tools.uploadImg(idcardFrontBase64, idcardFrontName);
            if (idcardFrontPath.equals("error"))
            {
                return ResultUtil.fail(-1, "身份证正面图片上传失败");
            }

            // 上传身份证反面图片
            String idcardBackName = "idcard_back_" + userId + ".jpg";
            String idcardBackPath = Tools.uploadImg(idcardBackBase64, idcardBackName);
            if (idcardBackPath.equals("error"))
            {
                return ResultUtil.fail(-1, "身份证反面图片上传失败");
            }

            person.setIdcardImgFront(idcardFrontPath);
            person.setIdcardImgBack(idcardBackPath);

            person.setAccountId(userId);
        }
        else
        {
            log.info("编辑实名信息start");
            if (!Tools.isEmpty(idcardFrontBase64))
            {
                // 上传身份证正面图片
                String idcardFrontName = "idcard_front_" + userId + ".jpg";
                String idcardFrontPath = Tools.uploadImg(idcardFrontBase64, idcardFrontName);
                if (idcardFrontPath.equals("error"))
                {
                    return ResultUtil.fail(-1, "身份证正面图片上传失败");
                }
                person.setIdcardImgFront(idcardFrontPath);
            }

            if (!Tools.isEmpty(idcardBackBase64))
            {
                // 上传身份证反面图片
                String idcardBackName = "idcard_back_" + userId + ".jpg";
                String idcardBackPath = Tools.uploadImg(idcardBackBase64, idcardBackName);
                if (idcardBackPath.equals("error"))
                {
                    return ResultUtil.fail(-1, "身份证反面图片上传失败");
                }
                person.setIdcardImgBack(idcardBackPath);
            }
        }
        person.setState(0);
        person.setCreatedTime(new Date());
        person.setUpdatedTime(new Date());

        return ResultUtil.success(personService.add(person));
    }

    /**
     * 根据accountId查询
     * @param request
     * @return
     */
    @GetMapping(value="/person")
    public Result<Person> findByAccountId(HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);
        return ResultUtil.success(personService.findByAccountId(accountId));
    }
}
