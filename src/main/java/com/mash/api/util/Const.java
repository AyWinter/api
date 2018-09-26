package com.mash.api.util;

public class Const {
    /**
     * OSS对象存储自定义域名
     */
    public static String OSS_BUCKET_WEB = "http://oss.hunchg.com/";

    /**
     * 审核状态  0  审核中   1  审核成功   2  审核失败
     */
    public static Integer EXAMINE_ING = 0;
    public static Integer EXAMINE_OK = 1;
    public static Integer EXAMINE_NG = 2;

    /**
     * KEY（达美）
     */
    public static String DM_KEY = "112B41FA2DF34815983FC5DD82831EFC";

    /**
     * 添加客户（达美）
     */
    public static String DM_API_URL_ADD_CUSTOMER = "http://dm.msplat.cn/api/client/add.htm";

    /**
     * 根据名称获取客户（达美）
     */
    public static String DM_API_URL_GET_CUSTOMER_BY_NAME = "http://dm.msplat.cn/api/client/getByClientName.htm";

    /**
     * 创建排期单（达美）
     */
    public static String DM_API_URL_CREATE_SCHEDULE = "http://dm.msplat.cn/api/ur/create.htm";

    /**
     * 取消排期单（达美）
     */
    public static String DM_API_URL_CANCEL_SCHEDULE = "http://dm.msplat.cn/api/ur/cancel.htm";

    /**
     * 将排期单置为已签约状态
     */
    public static String DM_API_URL_SIGN_SCHEDULE = "http://dm.msplat.cn/api/ur/signed.htm";

    /**
     * 达美账户ID
     */
    public static Integer DM_ACCOUNT_ID = 8;

    /**
     * 支付时间
     */
    public static Integer PAY_TIME = 30;
}
