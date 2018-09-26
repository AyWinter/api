package com.mash.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.mash.api.entity.Result;
import com.mash.api.entity.WechatUserInfo;
import com.mash.api.repository.WechatUserInfoRepository;
import com.mash.api.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;

@RestController
public class WechatAuthController
{
    private static final Logger log = LoggerFactory.getLogger(WechatAuthController.class);

    @Autowired
    private WechatUserInfoRepository wechatUserInfoRepository;

    /**
     * 获取openId
     * @param accessCode
     * @param response
     * @return
     */
    @GetMapping(value="/wechat/auth/{accessCode}")
    public Result getOpenIdByAccessCode(@PathVariable("accessCode")String accessCode,
                                        HttpServletResponse response)
    {
        log.info("根据accessCode{}获取openid start", accessCode);
        String openId = "";
        String getAccessTokenUrl = WechatConfig.AUTH_ACCESSCODE_URL;
        getAccessTokenUrl = getAccessTokenUrl.replace("APPID",
                WechatConfig.appid);
        getAccessTokenUrl = getAccessTokenUrl.replace("SECRET",
                WechatConfig.SECRET);
        getAccessTokenUrl = getAccessTokenUrl.replace("CODE", accessCode);

        try {
            String result = WebUtil.doGet(getAccessTokenUrl, null);
            log.info("get openId result = {}", result);
            JSONObject accessCodeInfo = JSONObject.parseObject(result);

            openId = accessCodeInfo.getString("openid");

            // 获取用户信息
            // 获取access_token
            String getAccessTokenUrl2 = WechatConfig.GET_ACCESS_TOKEN_URL;
            getAccessTokenUrl2 = getAccessTokenUrl2.replace("APPID", WechatConfig.appid);
            getAccessTokenUrl2 = getAccessTokenUrl2.replace("APPSECRET",WechatConfig.SECRET);
            String result2 = WebUtil.doGet(getAccessTokenUrl2, null);
            log.info("result2 = {}", result2);
            JSONObject accessCodeInfo2 = JSONObject.parseObject(result2);
            String access_token = accessCodeInfo2.getString("access_token");
            log.info("access_token = {}", access_token);

            WechatUserInfo wechatUserInfo = wechatUserInfoRepository.findByOpenId(openId);
            if (wechatUserInfo == null)
            {
                // 根据access_token和openId获取用户信息
                String getUserInfoUrl = WechatConfig.GET_USER_BASIC_INFO_URL;
                getUserInfoUrl = getUserInfoUrl.replace("ACCESS_TOKEN", access_token);
                getUserInfoUrl = getUserInfoUrl.replace("OPENID", openId);
                // 获取userInfo
                String userInfo = WebUtil.doGet(getUserInfoUrl, null);
                log.info("userInfo = {}", userInfo);
                JSONObject user = JSONObject.parseObject(userInfo);
                // 昵称
                String nickName = user.getString("nickname");
                // 头像
                String headImgUrl = user.getString("headimgurl");

                wechatUserInfo = new WechatUserInfo();
                wechatUserInfo.setOpenId(openId);
                wechatUserInfo.setNickName(nickName);
                wechatUserInfo.setHeadImgUrl(headImgUrl);

                wechatUserInfoRepository.save(wechatUserInfo);

                // 保存openId到memcache
                this.saveOpenIdToRedis(response, openId);

                log.info("openId = {}", openId);

                return ResultUtil.success();
            }
            else
            {
                Integer accountId = wechatUserInfo.getAccountId();
                String token = saveLoginToken(accountId, response);

                // 保存openId到memcache
                this.saveOpenIdToRedis(response, openId);
                if (accountId == null || accountId == 0)
                {
                    return ResultUtil.success();
                }
                else
                {
                    return ResultUtil.success(token);
                }
            }

            // 获取用户信息

//            return ResultUtil.success(openId + "_______" + access_token);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("未知异常：" + e);
            return ResultUtil.fail(-1, "未知异常");
        }
    }

    /**
     * 保存openId 到memcache
     * @param response
     * @param openId
     */
    public void saveOpenIdToRedis(HttpServletResponse response, String openId)
    {
        log.info("save openId to redis start");

        Calendar c = Calendar.getInstance();
        String token = Tools.encryptStrByMD5("openId" + c.getTime()
                + Tools.randomFourDigit());

        int expireTime = 3600*24;
        RedisUtil.setStr(token, openId, expireTime);
        Cookie cookie = new Cookie("openId", token);
        cookie.setMaxAge(1800);
        cookie.setHttpOnly(true);
        cookie.setDomain(".hunchg.com");
        cookie.setPath("/");

        response.addCookie(cookie);
        log.info("save openId to redis end");
    }

    /**
     * 保存登录令牌
     *
     * @param id
     * @param response
     */
    public String saveLoginToken(Integer id, HttpServletResponse response) {
        log.info("保存登录令牌start");
        Calendar c = Calendar.getInstance();
        String token = Tools.encryptStrByMD5(String.valueOf(id) + c.getTime());
        // 过期时间30分钟
        RedisUtil.setStr(token, String.valueOf(id), 3600*8);

        Cookie cookie = new Cookie("loginid", token);
        cookie.setMaxAge(3600*8);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);
        log.info("保存登录令牌end");
        return token;
    }
}
