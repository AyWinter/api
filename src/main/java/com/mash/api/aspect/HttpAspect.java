package com.mash.api.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class HttpAspect {

    private static final Logger log = LoggerFactory.getLogger(HttpAspect.class);

    @Pointcut("execution(public * com.mash.api.controller.AccountController.*(..))")
    public void log()
    {

    }

    @Before("log()")
    public void before(JoinPoint joinPoint)
    {
        log.info("请求处理start");
        ServletRequestAttributes attribute = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attribute.getRequest();

        // url
        log.info("url={}", request.getRequestURL());
        // method
        log.info("method={}", request.getMethod());
        // ip
        log.info("ip={}", request.getRemoteAddr());
        // class_method
        log.info("class_method={}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        // args
        log.info("args={}", joinPoint.getArgs());
    }

    @After("log()")
    public void after()
    {
        log.info("请求处理end");
    }

    @AfterReturning(returning = "object", pointcut = "log()")
    public void doAfterReturning(Object object)
    {
        log.info("response={}", object);
    }
}
