package com.mash.api.util;

import com.mash.api.controller.AccountController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Set;

public class RedisUtil {

    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);

    //Redis服务器IP
    private static String ADDR = "127.0.0.1";

    //Redis的端口号
    private static int PORT = 6379;

    //访问密码
    private static String AUTH = "admin";

    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = 2;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 200;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 10000;

    private static int TIMEOUT = 10000;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;

    private static JedisPool jedisPool = null;

    /**
     * 初始化Redis连接池
     */
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxActive(MAX_ACTIVE);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWait(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config, ADDR, PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Jedis实例
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 释放jedis资源
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
    }

    /**
     *
     * 将对象以hash形式保存到redis
     * @param key
     * @param o
     */
    public static void setObjectToHash(String key, Object o)
    {
        Jedis jedis = RedisUtil.getJedis();

        Class c = o.getClass();
        Field[] fieldArray = c.getDeclaredFields();
        for (int i = 0; i < fieldArray.length; i ++)
        {
            Field f = fieldArray[i];
            f.setAccessible(true);
            // 属性名称
            String fieldName = f.getName();
            log.info("fieldName = {}", fieldName);
            Object value = new Object();
            try {
                // 属性值
                value = f.get(o);
                log.info("fieldValue = {}", value);

                jedis.hset(key, fieldName, value.toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 存储对象
     * @param key
     * @param object
     */
    public static void setObject(String key, Object object)
    {
        log.info("保存object到redis开始");
        log.info("key = {}", key);
        log.info("object = {}", object);
        Jedis jedis = RedisUtil.getJedis();

        jedis.set(key.getBytes(), RedisUtil.serialize(object));
        // 释放链接
        RedisUtil.returnResource(jedis);
        log.info("保存object到redis结束");
    }

    /**
     *
     * @param key
     * @return
     */
    public static Object getObject(String key)
    {
        Jedis jedis = RedisUtil.getJedis();
        byte[] b = jedis.get(key.getBytes());
        Object o = null;
        if (b != null)
        {
            o = RedisUtil.unserizlize(b);
        }
        RedisUtil.returnResource(jedis);
        return o;
    }

    /**
     *
     * 根据key获取object
     * @param key
     * @return
     */
    public static Object getStr(String key)
    {
        Jedis jedis = RedisUtil.getJedis();
        Object o = jedis.get(key);
        // 释放链接
        RedisUtil.returnResource(jedis);
        return o;
    }

    /**
     * 保存字符串到redis
     * @param key
     * @param value
     * @param expire 过期时间  -1 永不过期
     */
    public static void setStr(String key, String value, int expire)
    {
        Jedis jedis = RedisUtil.getJedis();

        jedis.set(key, value);
        if (expire != -1)
        {
            jedis.expire(key, expire);
        }

        RedisUtil.returnResource(jedis);
    }

    /**
     * 对象序列化
     * @param obj
     * @return
     */
    public static byte [] serialize(Object obj){
        ObjectOutputStream oos=null;
        ByteArrayOutputStream baos=null;
        try {
            baos=new ByteArrayOutputStream();
            oos=new ObjectOutputStream(baos);
            oos.writeObject(obj);
            byte[] byt=baos.toByteArray();
            return byt;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
                oos.close();;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * 反序列化
     * @param byt
     * @return
     */
    public static Object unserizlize(byte[] byt){
        ObjectInputStream oii=null;
        ByteArrayInputStream bis=null;
        bis=new ByteArrayInputStream(byt);
        try {
            oii=new ObjectInputStream(bis);
            Object obj=oii.readObject();
            return obj;
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            try {
                oii.close();
                bis.close();;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public static void main(String[] args)
    {
    	Jedis redis = RedisUtil.getJedis();
    }

    	// map
		// redis.hset(key, field, value)
		// redis.hget(key, field)
		//
		// // list
		// redis.rpush(key, strings)
		// redis.lpush(key, strings)
		// redis.lrange(key, start, end)
		// redis.l
		//
		// // set
		// redis.sadd(key, members)
		//
		// // sort set
		// redis.zadd(key, score, member)
		//
		//
		// redis
}
