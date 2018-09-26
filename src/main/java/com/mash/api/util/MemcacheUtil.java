package com.mash.api.util;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.internal.OperationFuture;

public class MemcacheUtil {

    private static final String host = "m-wz9e82e9868547d4.memcache.rds.aliyuncs.com";
    private static final String port = "11211";
    private static final String username = "m-wz9e82e9868547d4";
    private static final String password = "KunrnAd900609";
    private static MemcachedClient cache = null;

    // 受保护的对象
    protected static MemcacheUtil instance = null;

    /**
     * 为受保护的对象提供一个公共的访问方法
     */
    public static MemcacheUtil getInstance() {
        if (instance != null) {
            return instance;
        } else {
            return new MemcacheUtil();
        }
    }

    static {
        try {
//			AuthDescriptor ad = new AuthDescriptor(new String[] { "PLAIN" },
//					new PlainCallbackHandler(username, password));
//			cache = new MemcachedClient(
//					new ConnectionFactoryBuilder().setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
//							.setAuthDescriptor(ad).build(),
//					AddrUtil.getAddresses(host + ":" + port));
            cache = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil.getAddresses(host + ":" + port));
        } catch (IOException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
        }
    }

    /**
     * @param key
     * @param expireTime
     *            过期时间
     * @param value
     */
    public static void setValue(String key, int expireTime, Object value) {
        OperationFuture<Boolean> future = cache.set(key, expireTime, value);
        try {
            future.get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param key
     * @return object
     */
    public static Object getValue(String key) {
        return cache.get(key);
    }

    /**
     * @param key
     */
    public static void deleteValue(String key) {
        cache.delete(key);
    }

    public static void main(String[] args) {

        setValue("title", 10, "this is titlle");

        String value = (String) getValue("title");
        System.out.println(value);

    }
}
