package com.mash.api.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

public class WebUtil {

    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String METHOD_GET = "GET";

    private static class DefaultTrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    }

    public static String doPost(String requestUrl, Map<String, Object> map) {
        // ʹ��HttpURLConnection����http����

        try {
            String rqestXml = mapToXml(map);

            System.out.println(rqestXml);

            byte[] xmlbyte = rqestXml.getBytes("UTF-8");
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setDoOutput(true);//
            conn.setUseCaches(false);//
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");//
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Length",
                    String.valueOf(xmlbyte.length));
            conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            DataOutputStream outStream = new DataOutputStream(
                    conn.getOutputStream());
            outStream.write(xmlbyte);//
            outStream.flush();
            outStream.close();

            // ������������xml��Ϣ��
            byte[] msgBody = null;
            if (conn.getResponseCode() != 200)
                throw new RuntimeException("����urlʧ��");
            InputStream is = conn.getInputStream();// ��ȡ�������

            byte[] temp = new byte[1024];
            int n = 0;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((n = is.read(temp)) != -1) {
                bos.write(temp, 0, n);
            }
            msgBody = bos.toByteArray();
            bos.close();
            is.close();
            String returnXml = new String(msgBody, "UTF-8").trim();

            conn.disconnect();

            //
            System.out.println(returnXml);

            return returnXml;
        } catch (Exception ex) {

        }
        return null;
    }

    /**
     * map ת xml
     *
     * @param map
     * @return xml
     */
    public static String mapToXml(Map<String, Object> map) {

        StringBuffer sb = new StringBuffer();

        sb.append("<xml>");

        Set set = map.keySet();
        for (Iterator it = set.iterator(); it.hasNext();) {
            String key = (String) it.next();
            Object value = map.get(key);
            if (null == value)
                value = "";
            if (value.getClass().getName().equals("java.util.ArrayList")) {
                ArrayList list = (ArrayList) map.get(key);
                sb.append("<" + key + ">");
                for (int i = 0; i < list.size(); i++) {
                    HashMap hm = (HashMap) list.get(i);
                    mapToXml(hm);
                }
                sb.append("</" + key + ">");

            } else {
                if (value instanceof HashMap) {
                    sb.append("<" + key + ">");
                    mapToXml((HashMap) value);
                    sb.append("</" + key + ">");
                } else {
                    sb.append("<" + key + ">" + value + "</" + key + ">");
                }

            }

        }
        sb.append("</xml>");
        return sb.toString();
    }

    public static String getWechatprepayId(String resXml) {
        String codeUrl = "";
        Document d = null;

        try {
            d = DocumentHelper.parseText(resXml);
        } catch (Exception ex) {

        }

        if (d != null) {
            Element root = d.getRootElement();

            if (root != null) {
                Element ecodeurl = root.element("prepay_id");

                codeUrl = ecodeurl.getText();
            }

        }
        return codeUrl;
    }

    public static String doGet(String url, Map<String, String> params)
            throws IOException {
        return doGet(url, params, DEFAULT_CHARSET);
    }

    /**
     * 执行HTTP GET请求。
     *
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @param charset
     *            字符集，如UTF-8, GBK, GB2312
     * @return 响应字符串
     * @throws IOException
     */
    public static String doGet(String url, Map<String, String> params,
                               String charset) throws IOException {
        HttpURLConnection conn = null;
        String rsp = null;

        try {
            String ctype = "application/x-www-form-urlencoded;charset="
                    + charset;
            String query = buildQuery(params, charset);
            try {
                conn = getConnection(buildGetUrl(url, query), METHOD_GET, ctype);
            } catch (IOException e) {
                throw e;
            }

            try {
                rsp = getResponseAsString(conn);
            } catch (IOException e) {
                throw e;
            }

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return rsp;
    }

    public static String buildQuery(Map<String, String> params, String charset)
            throws IOException {
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder query = new StringBuilder();
        Set<Entry<String, String>> entries = params.entrySet();
        boolean hasParam = false;

        for (Entry<String, String> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue();
            // 忽略参数名或参数值为空的参数
            if (!Tools.isEmpty(name) || !Tools.isEmpty(value)) {
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }

                query.append(name).append("=")
                        .append(URLEncoder.encode(value, charset));
            }
        }

        return query.toString();
    }

    private static URL buildGetUrl(String strUrl, String query)
            throws IOException {
        URL url = new URL(strUrl);
        if (StringUtils.isEmpty(query)) {
            return url;
        }

        if (StringUtils.isEmpty(url.getQuery())) {
            if (strUrl.endsWith("?")) {
                strUrl = strUrl + query;
            } else {
                strUrl = strUrl + "?" + query;
            }
        } else {
            if (strUrl.endsWith("&")) {
                strUrl = strUrl + query;
            } else {
                strUrl = strUrl + "&" + query;
            }
        }

        return new URL(strUrl);
    }

    private static HttpURLConnection getConnection(URL url, String method,
                                                   String ctype) throws IOException {
        HttpURLConnection conn = null;
        if ("https".equals(url.getProtocol())) {
            SSLContext ctx = null;
            try {
                ctx = SSLContext.getInstance("TLS");
                ctx.init(new KeyManager[0],
                        new TrustManager[] { new DefaultTrustManager() },
                        new SecureRandom());
            } catch (Exception e) {
                throw new IOException(e);
            }
            HttpsURLConnection connHttps = (HttpsURLConnection) url
                    .openConnection();
            connHttps.setSSLSocketFactory(ctx.getSocketFactory());
            connHttps.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return false;// 默认认证不通过，进行证书校验。
                }
            });
            conn = connHttps;
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }

        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
        conn.setRequestProperty("User-Agent", "aop-sdk-java");
        conn.setRequestProperty("Content-Type", ctype);
        return conn;
    }

    protected static String getResponseAsString(HttpURLConnection conn)
            throws IOException {
        String charset = getResponseCharset(conn.getContentType());
        InputStream es = conn.getErrorStream();
        if (es == null) {
            return getStreamAsString(conn.getInputStream(), charset);
        } else {
            String msg = getStreamAsString(es, charset);
            if (StringUtils.isEmpty(msg)) {
                throw new IOException(conn.getResponseCode() + ":"
                        + conn.getResponseMessage());
            } else {
                throw new IOException(msg);
            }
        }
    }

    private static String getResponseCharset(String ctype) {
        String charset = DEFAULT_CHARSET;

        if (!StringUtils.isEmpty(ctype)) {
            String[] params = ctype.split(";");
            for (String param : params) {
                param = param.trim();
                if (param.startsWith("charset")) {
                    String[] pair = param.split("=", 2);
                    if (pair.length == 2) {
                        if (!StringUtils.isEmpty(pair[1])) {
                            charset = pair[1].trim();
                        }
                    }
                    break;
                }
            }
        }

        return charset;
    }

    private static String getStreamAsString(InputStream stream, String charset)
            throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    stream, charset));
            StringWriter writer = new StringWriter();

            char[] chars = new char[256];
            int count = 0;
            while ((count = reader.read(chars)) > 0) {
                writer.write(chars, 0, count);
            }

            return writer.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * 微信支付返回结构xml
     * @param request
     * @return
     */
    public static SortedMap<String, Object> parseXml(HttpServletRequest request) {
        SortedMap<String, Object> map = new TreeMap<String, Object>();
        try {
            InputStream inputStream = request.getInputStream();
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            Element root = document.getRootElement();
            @SuppressWarnings("unchecked")
            List<Element> elementList = root.elements();
            for (Element e : elementList)
                map.put(e.getName(), e.getText());

            inputStream.close();
            inputStream = null;
        } catch (Exception exs) {

        }
        return map;
    }

    /**
     * httpEntity to map
     * @param entity
     * @return map
     */
    public static Map<String, Object> entityToMap(HttpEntity entity)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        SAXReader saxReader = new SAXReader();
        Document doc;

        try {
            doc = saxReader.read(entity.getContent());
            Element root = doc.getRootElement();
            List children = root.elements();
            if(children != null && children.size() > 0) {
                for(int i = 0; i < children.size(); i++) {
                    Element child = (Element)children.get(i);
                    map.put(child.getName(), child.getTextTrim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     *
     * @param url
     * @param paramsList
     * @return
     */
    public static JSONObject post2(String url, List<NameValuePair> paramsList)
    {
        JSONObject resultJson = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            // 转换为键值对
            String params = EntityUtils.toString(new UrlEncodedFormEntity(paramsList, Consts.UTF_8));
            // 请求地址
            url += "?" + params;
            // 创建httpPost.
            HttpPost httpPost = new HttpPost(url);
            // 通过请求对象获取响应对象
            response = httpclient.execute(httpPost);
            // 判断网络连接状态码是否正常(0--200都数正常)
            if (response.getStatusLine().getStatusCode() == 200) {
                String result= EntityUtils.toString(response.getEntity(), "utf-8");
                resultJson = JSONObject.fromObject(result);
                // JSONObject data = JSONObject.fromObject(resultJson.get("data"));

                return resultJson;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

//    /**
//     * 查询点位已被占用的档期数组
//     * @param url
//     * @param paramsList
//     * @return
//     */
//    public static JSONArray post2(String url, List<NameValuePair> paramsList)
//    {
//        JSONObject resultJson = null;
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        CloseableHttpResponse response = null;
//        try {
//            // 转换为键值对
//            String params = EntityUtils.toString(new UrlEncodedFormEntity(paramsList, Consts.UTF_8));
//            // 请求地址
//            url += "?" + params;
//            // 创建httpPost.
//            HttpPost httpPost = new HttpPost(url);
//            // 通过请求对象获取响应对象
//            response = httpclient.execute(httpPost);
//            // 判断网络连接状态码是否正常(0--200都数正常)
//            if (response.getStatusLine().getStatusCode() == 200) {
//                String result= EntityUtils.toString(response.getEntity(), "utf-8");
//                resultJson = JSONObject.fromObject(result);
//
//                JSONArray data = (JSONArray) resultJson.get("data");
//
//                return data;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (response != null) {
//                    response.close();
//                }
//                httpclient.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
}
