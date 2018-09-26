package com.mash.api.entity;

public class WechatPayParams {

    private String sign;
    private String wechatPackage;
    private String appId;
    private String timeStamp;
    private String nonceStr;
    private String signType;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getWechatPackage() {
        return wechatPackage;
    }

    public void setWechatPackage(String wechatPackage) {
        this.wechatPackage = wechatPackage;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }
}
