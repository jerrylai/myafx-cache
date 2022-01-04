package cn.myafx.cache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import cn.myafx.cache.base.IHashCache;
import cn.myafx.cache.base.IRedisCache;
import cn.myafx.cache.base.IValueCache;

public final class RedisUtils {
    private static final Object lockRedis = new Object();
    private static RedisConnectionFactory connectionFactory;
    private static final Object lockCacheKey = new Object();
    private static ICacheKey cacheKey;
    private static String prefix;
    private static final Object lockCacheFactory = new Object();
    private  static CacheFactory cacheFactory;

    private static ObjectMapper mapper = new ObjectMapper()
    .setSerializationInclusion(Include.NON_NULL)
    .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
    .setTimeZone(TimeZone.getTimeZone("GMT+8"));

    public static ObjectMapper getObjectMapper(){
        return mapper;
    }
    
    private static RedisConnectionFactory getFactory() throws Exception {
        if(connectionFactory != null) return connectionFactory;
        synchronized(lockRedis){
            if(connectionFactory == null){
                var hostConfig = "192.168.1.231:6379";
                if(hostConfig == null || hostConfig.isEmpty()) throw new Exception("redis.host is null!");
                var hostArr = hostConfig.split(",");
                List<String> hostList = new ArrayList<>(hostArr.length);
                for (String h : hostArr) {
                    if(!h.isEmpty()) hostList.add(h);
                }
                var redisConfig = new RedisClusterConfiguration(hostList);
                var password = "";
                if(password != null && !password.isEmpty()) redisConfig.setPassword(password);
                
                var clientConfig = LettuceClientConfiguration.builder();
                var clientName = "test";
                if(clientName != null && !clientName.isEmpty()) clientConfig.clientName(clientName);

                var factory = new LettuceConnectionFactory(redisConfig, clientConfig.build());
                factory.afterPropertiesSet();
                connectionFactory = factory;
            }
        }

        return connectionFactory;
    }

    public static RedisConnection getConnection()throws Exception {
        var factory = getFactory();

        return factory.getClusterConnection();
    }

    private static ICacheKey getCacheKey() throws Exception{
        if(cacheKey != null) return cacheKey;
        synchronized(lockCacheKey){
            if(cacheKey == null){
                try(var fs = RedisUtils.class.getClassLoader().getResourceAsStream("config/cache-key.xml")){
                    cacheKey = new cn.myafx.cache.CacheKey(fs);
                }
            }
        }

        return cacheKey;
    }

    private static String getPrefix() throws Exception {

        if(prefix != null) return prefix;

        var s = "test:";
        if(s == null) s = "";
        prefix = s;

        return prefix;
    }

    private static CacheFactory getCacheFactory() throws Exception{
        if(cacheFactory != null) return cacheFactory;
        synchronized(lockCacheFactory){
            cacheFactory = new CacheFactory(getFactory(), getCacheKey(), getPrefix(), getObjectMapper());
        }

        return cacheFactory;
    }

    public static <T extends IRedisCache> T getCache(String item, Class<T> clazz) throws Exception {
        T cache = getCacheFactory().getCache(item, clazz);

        return cache;
    }

    public static <T extends IValueCache<TValue>, TValue> T getCache(String item, Class<T> clazz, Class<TValue> valueClass) throws Exception {
        T cache = getCacheFactory().getCache(item, clazz,valueClass);

        return cache;
    }

    public static <T extends IHashCache<TField, TValue>, TField, TValue> T getCache(String item, Class<T> clazz, Class<TField> fieldClass, Class<TValue> valueClass) throws Exception {
        T cache = getCacheFactory().getCache(item, clazz, fieldClass, valueClass);

        return cache;
    }
}
