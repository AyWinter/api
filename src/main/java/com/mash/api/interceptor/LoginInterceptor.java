package com.mash.api.interceptor;

import com.mash.api.annotation.Auth;
import com.mash.api.controller.AccountController;
import com.mash.api.exception.MyException;
import com.mash.api.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        HandlerMethod handlerMethod = null;
        Method method = null;
        Class<?> clazz = null;
        try
        {
            handlerMethod = (HandlerMethod) handler;
            method = handlerMethod.getMethod();
            clazz = method.getDeclaringClass();
        }
        catch (Exception ex)
        {
            return true;
        }

        if (clazz.isAnnotationPresent(Auth.class) ||
                method.isAnnotationPresent(Auth.class))
        {
            Cookie[] cookies = request.getCookies();
            if (cookies == null)
            {
                log.info("用户未登录");
                throw new MyException(-2, "未登录");
            }
            Cookie loginCookie = null;
            for (Cookie cookie : cookies)
            {
                if ("loginid".equals(cookie.getName()))
                {
                    loginCookie = cookie;
                    break;
                }
            }

            if (loginCookie == null)
            {
                log.info("用户未登录");
                throw new MyException(-2, "未登录");
            }

            String value = loginCookie.getValue();
            if (value == null || "".equals(value))
            {
                log.info("用户未登录");
                throw new MyException(-2, "未登录");
            }
            else
            {
                Object loginToken = RedisUtil.getStr(value);
                if (loginToken == null)
                {
                    log.info("用户未登录");
                    throw new MyException(-2, "未登录");
                }
                else
                {
                    log.info("用户已登录");
                    return true;
                }
            }
        }
        return true;
    }
}
