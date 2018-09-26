package com.mash.api.handle;

import com.mash.api.controller.AccountController;
import com.mash.api.entity.Result;
import com.mash.api.exception.MyException;
import com.mash.api.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ExceptionHandle {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandle.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result handle(Exception e)
    {
         if (e instanceof MyException)
         {
             MyException testException = (MyException)e;
             return ResultUtil.fail(testException.getCode(), testException.getMessage());
         }
         else
         {
             log.info(e.getMessage());
             log.info(e.getLocalizedMessage());
             return ResultUtil.fail(-1, "未知异常");
         }
    }
}
