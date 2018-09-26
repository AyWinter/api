package com.mash.api.util;

public class WechatConfig {

    public static String appid = "wxd40cc8f0622fda33";

    public static String mchid = "1486156322";

    // 币种
    public static String feetype = "CNY";

    public static String SECRET = "0b356825635c961011b4a5c1e6f10ffe";

    public static String redirect_url = "http://service.hunchg.com/#/wechat/auth";

    public static String wechat_notify_url = "http://api.hunchg.com/wechatpay/notify";

    // ַ 统一下单地址
    public static String place_order_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    //
    public static String limit_pay = "no-credit";

    // openid
    public static String openid = "";

    /**
     * 网页授权code获取 URL
     */
    public static final String AUTH_USER_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";

    /**
     * 网页授权access_token URL
     */
    public static final String AUTH_ACCESSCODE_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

    /**
     * 获取用户基本信息（包括UnionID机制） URL
     */
    public static final String GET_USER_BASIC_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

    /**
     * 获取请求ACCESS_TOKEN URL
     */
    public static final String GET_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    /**
     * 获取用户详细信息
     */
    public static final String GET_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

    /**
     * 应用授权作用域：不弹出授权页面
     */
    public static final String SCOPE_BASE = "snsapi_base";

    /**
     * 应用授权作用域：弹出授权页面
     */
    public static final String SCOPE_USERINFO = "snsapi_userinfo";

    public static final String WECHAT_REFUND_SIGN_CERT = "D:\\hunchg\\wechatPay\\apiclient_cert.p12";

    /**
     * 退款接口
     */
    public static final String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
}
