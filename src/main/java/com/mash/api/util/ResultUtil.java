package com.mash.api.util;

import com.mash.api.entity.Result;

public class ResultUtil {

    public static Result success(Object object)
    {
        Result result = new Result();
        result.setCode(0);
        result.setMsg("success");
        result.setData(object);

        return result;
    }

    public static Result success()
    {
        Result result = new Result();
        result.setCode(0);
        result.setMsg("success");

        return result;
    }

    public static Result fail(int code, String msg)
    {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);

        return result;
    }
}
