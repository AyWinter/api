package com.mash.api.controller;

import com.mash.api.util.Const;
import com.mash.api.util.WebUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Test {

//    public static void main(String[] args)
//    {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//        List<String> periods = new ArrayList<String>();
//        Calendar calendar = Calendar.getInstance();
//
//        String filed1 = "";
//        String filed2 = "";
//        String filed3 = "";
//        String filed4 = "";
//
//        Integer day = calendar.get(Calendar.DATE);
//        Integer currentMonthDays = getCurrentMonthLastDay(Calendar.getInstance());
//
//        if (day + 3 <= 14)
//        {
//
//            filed1 = sdf.format(calendar.getTime());
//
//            calendar.set(Calendar.DATE, 14);
//            filed1 += "=" + sdf.format(calendar.getTime());
//
//            ///////////////////////////////////////////
//            calendar.set(Calendar.DATE, 16);
//            filed2 = sdf.format(calendar.getTime());
//            currentMonthDays = getCurrentMonthLastDay(calendar);
//            calendar.set(Calendar.DATE, currentMonthDays - 1);
//
//            filed2 += "=" + sdf.format(calendar.getTime());;
//
//            ///////////////////////////////////////////
//            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
//            calendar.set(Calendar.DATE, 1);
//            filed3 = sdf.format(calendar.getTime());
//            calendar.set(Calendar.DATE, 14);
//            filed3 += "=" + sdf.format(calendar.getTime());;
//
//            ////////////////////////////////////////////
//            calendar.set(Calendar.DATE, 16);
//            filed4 = sdf.format(calendar.getTime());
//            currentMonthDays = getCurrentMonthLastDay(calendar);
//            calendar.set(Calendar.DATE, currentMonthDays - 1);
//
//            filed4 += "=" + sdf.format(calendar.getTime());;
//        }
//        else if (currentMonthDays - day >= 4)
//        {
//            filed1 = sdf.format(calendar.getTime());
//
//            currentMonthDays = getCurrentMonthLastDay(calendar);
//            calendar.set(Calendar.DATE, currentMonthDays - 1);
//            filed1 += "=" + sdf.format(calendar.getTime());
//
//            ///////////////////////////////////////////
//            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
//            calendar.set(Calendar.DATE, 1);
//            filed2 = sdf.format(calendar.getTime());
//            calendar.set(Calendar.DATE, 14);
//            filed2 += "=" + sdf.format(calendar.getTime());;
//
//            ////////////////////////////////////////////
//            calendar.set(Calendar.DATE, 16);
//            filed3 = sdf.format(calendar.getTime());
//            currentMonthDays = getCurrentMonthLastDay(calendar);
//            calendar.set(Calendar.DATE, currentMonthDays - 1);
//
//            filed3 += "=" + sdf.format(calendar.getTime());;
//
//            ////////////////////////////////////////////
//            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
//            calendar.set(Calendar.DATE, 1);
//            filed4 = sdf.format(calendar.getTime());
//            calendar.set(Calendar.DATE, 14);
//            filed4 += "=" + sdf.format(calendar.getTime());;
//        }
//        System.out.println(filed1);
//        System.out.println(filed2);
//        System.out.println(filed3);
//        System.out.println(filed4);
//    }
//
//    public static int getCurrentMonthLastDay(Calendar calendar)
//    {
//        calendar.set(Calendar.DATE, 1);//把日期设置为当月第一天
//        calendar.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
//        int maxDate = calendar.get(Calendar.DATE);
//        return maxDate;
//    }

    public static void main(String[] args)
    {

//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("clientNumber", "KH18090311422761");
//
//        JSONArray points = new JSONArray();
//        JSONObject point = new JSONObject();
//        point.put("customNumber", "H0095-01");
//        point.put("etime", "20180920");
//        point.put("number", 1);
//        point.put("stime", "20180915");
//
//        points.add(point);
//
//        jsonObject.put("point", points);
//        jsonObject.put("remark", "测试排期单");
//        jsonObject.put("title", "测试排期单");
//
//        String dataStr = jsonObject.toString();
//        System.out.println(dataStr);
//
//        List<NameValuePair> paramsList = new ArrayList();
//
//        paramsList.add(new BasicNameValuePair("key", Const.DM_KEY));
//        paramsList.add(new BasicNameValuePair("data", dataStr));
//
//        JSONObject result = WebUtil.post2(Const.DM_API_URL_CREATE_SCHEDULE, paramsList);
//
//        System.out.println(result);

//        JSONObject result = cancel();
//        System.out.println(result);


    }

    public static JSONObject cancel()
    {
        List<NameValuePair> paramsList = new ArrayList();

        paramsList.add(new BasicNameValuePair("key", Const.DM_KEY));
        paramsList.add(new BasicNameValuePair("usingNumber", "UR18090411240833"));

        JSONObject result = WebUtil.post2(Const.DM_API_URL_CANCEL_SCHEDULE, paramsList);

        return result;
    }

    public static String getJsonData(JSONObject jsonParam, String urls) {
        StringBuffer sb=new StringBuffer();
        try {
            ;
            // 创建url资源
            URL url = new URL(urls);
            // 建立http连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置允许输出
            conn.setDoOutput(true);
            // 设置允许输入
            conn.setDoInput(true);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("POST");
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            // 转换为字节数组
            byte[] data = (jsonParam.toString()).getBytes();
            // 设置文件长度
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            // 设置文件类型:
            conn.setRequestProperty("contentType", "application/json");
            // 开始连接请求
            conn.connect();
            OutputStream out = new DataOutputStream(conn.getOutputStream()) ;
            // 写入请求的字符串
            out.write((jsonParam.toString()).getBytes());
            out.flush();
            out.close();

            System.out.println(conn.getResponseCode());

            // 请求返回的状态
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()){
                System.out.println("连接成功");
                // 请求返回的数据
                InputStream in1 = conn.getInputStream();
                try {
                    String readLine=new String();
                    BufferedReader responseReader=new BufferedReader(new InputStreamReader(in1,"UTF-8"));
                    while((readLine=responseReader.readLine())!=null){
                        sb.append(readLine).append("\n");
                    }
                    responseReader.close();
                    System.out.println(sb.toString());

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            else
            {

            }

        } catch (Exception e) {

        }

        return sb.toString();

    }
}
