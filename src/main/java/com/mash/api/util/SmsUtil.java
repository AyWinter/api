package com.mash.api.util;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;

public class SmsUtil {

    /**
     *
     * @param msg
     *            短信内容
     * @param mobileNo
     *            手机号码，多个号码使用","分割
     * @return 返回值定义参见HTTP协议文档
     * @throws Exception
     */
    public static String batchSend(String msg, String mobileNo)
            throws Exception {
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod();

        String url = "http://222.73.117.158/msg/";
        boolean needstatus = true;
		/*String msg = "您的验证码是" + verifyCode
				+ "。请勿向任何人提供您收到的短信验证码。";*/
        String extno = "1446";
        try {
            URI base = new URI(url, false);
            method.setURI(new URI(base, "HttpBatchSendSM", false));
            method.setQueryString(new NameValuePair[] {
                    new NameValuePair("account", "hnkrxx_ad"),
                    new NameValuePair("pswd", "Ad900609"),
                    new NameValuePair("mobile", mobileNo),
                    new NameValuePair("needstatus", String.valueOf(needstatus)),
                    new NameValuePair("msg", msg),
                    new NameValuePair("extno", extno), });
            int result = client.executeMethod(method);
            if (result == HttpStatus.SC_OK) {
                InputStream in = method.getResponseBodyAsStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }

                String sendResult = URLDecoder.decode(baos.toString(), "UTF-8");

                String[] sendResultArray = sendResult.split("\n");
                String[] stateArray = sendResultArray[0].split(",");
                return stateArray[1];
            } else {
                throw new Exception("HTTP ERROR Status: "
                        + method.getStatusCode() + ":" + method.getStatusText());
            }
        } finally {
            method.releaseConnection();
        }

    }
}
