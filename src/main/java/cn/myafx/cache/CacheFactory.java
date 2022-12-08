package cn.myafx.cache;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import cn.myafx.cache.base.IHashCache;
import cn.myafx.cache.base.IRedisCache;
import cn.myafx.cache.base.IValueCache;
import cn.myafx.cache.base.RedisCache;

/**
 * 
 */
public class CacheFactory implements AutoCloseable {
    private RedisConnectionFactory connectionFactory;
    private ICacheKey cacheKey;
    private String prefix;
    private IJsonMapper mapper;

    public CacheFactory(RedisConnectionFactory connectionFactory, ICacheKey cacheKey, String prefix,
            IJsonMapper jsonMapper) throws Exception {
        if (connectionFactory == null)
            throw new Exception("connectionFactory is null!");
        if (cacheKey == null)
            throw new Exception("cacheKey is null!");
        if (jsonMapper == null)
            throw new Exception("jsonMapper is null!");
        this.connectionFactory = connectionFactory;
        this.cacheKey = cacheKey;
        this.prefix = prefix;
        if (this.prefix == null)
            this.prefix = "";
        this.mapper = jsonMapper;
        RedisCache.DefaultJsonMapper = jsonMapper;
    }

    public RedisConnection getConnection() throws Exception {

        return connectionFactory.getClusterConnection();
    }

    public ICacheKey getCacheKey() {
        return this.cacheKey;
    }

    public String getPrefix() {
        return prefix;
    }

    public IJsonMapper getObjectMapper() {
        return mapper;
    }

    @SuppressWarnings("unchecked")
    public <T extends IRedisCache> T getCache(String item, Class<T> clazz) throws Exception {
        if (item == null || item.isEmpty())
            throw new Exception("item is null!");
        if (clazz == null)
            throw new Exception("clazz is null!");
        var arr = item.split(":");
        if (arr.length > 2)
            throw new Exception("item=" + item + " is error!");
        Class<?> impClass = clazz;
        var sname = clazz.getSimpleName();
        if (sname.startsWith("I")) {
            var name = clazz.getName();
            var className = name.substring(0, name.length() - sname.length()) + sname.substring(1);
            impClass = Class.forName(className);
        }
        if (impClass == null)
            throw new Exception("clazz(" + clazz.getName() + ") is error!");
        T cache = null;
        if (arr.length == 1) {
            var c = impClass.getConstructor(String.class, RedisConnection.class, ICacheKey.class, String.class);
            if (c == null)
                throw new Exception("clazz(" + clazz.getName() + ") is error!");
            cache = (T) c.newInstance(item, getConnection(), getCacheKey(), getPrefix());
        } else {
            var c = impClass.getConstructor(String.class, String.class, RedisConnection.class, ICacheKey.class,
                    String.class);
            if (c == null)
                throw new Exception("clazz(" + clazz.getName() + ") is error!");
            cache = (T) c.newInstance(arr[0], arr[1], getConnection(), getCacheKey(), getPrefix());
        }

        return cache;
    }

    @SuppressWarnings("unchecked")
    public <T extends IValueCache<TValue>, TValue> T getCache(String item, Class<T> clazz, Class<TValue> valueClass)
            throws Exception {
        if (item == null || item.isEmpty())
            throw new Exception("item is null!");
        if (clazz == null)
            throw new Exception("clazz is null!");
        if (valueClass == null)
            throw new Exception("valueClass is null!");
        var arr = item.split(":");
        if (arr.length > 2)
            throw new Exception("item=" + item + " is error!");
        Class<?> impClass = clazz;
        var sname = clazz.getSimpleName();
        if (sname.startsWith("I")) {
            var name = clazz.getName();
            var className = name.substring(0, name.length() - sname.length()) + sname.substring(1);
            impClass = Class.forName(className);
        }
        if (impClass == null)
            throw new Exception("clazz(" + clazz.getName() + ") is error!");
        T cache = null;
        if (arr.length == 1) {
            var c = impClass.getConstructor(String.class, RedisConnection.class, ICacheKey.class, String.class,
                    Class.class);
            if (c == null)
                throw new Exception("clazz(" + clazz.getName() + ") is error!");
            cache = (T) c.newInstance(item, getConnection(), getCacheKey(), getPrefix(), valueClass);
        } else {
            var c = impClass.getConstructor(String.class, String.class, RedisConnection.class, ICacheKey.class,
                    String.class, Class.class);
            if (c == null)
                throw new Exception("clazz(" + clazz.getName() + ") is error!");
            cache = (T) c.newInstance(arr[0], arr[1], getConnection(), getCacheKey(), getPrefix(), valueClass);
        }

        return cache;
    }

    @SuppressWarnings("unchecked")
    public <T extends IHashCache<TField, TValue>, TField, TValue> T getCache(String item, Class<T> clazz,
            Class<TField> fieldClass, Class<TValue> valueClass) throws Exception {
        if (item == null || item.isEmpty())
            throw new Exception("item is null!");
        if (clazz == null)
            throw new Exception("clazz is null!");
        if (fieldClass == null)
            throw new Exception("fieldClass is null!");
        if (valueClass == null)
            throw new Exception("valueClass is null!");
        var arr = item.split(":");
        if (arr.length > 2)
            throw new Exception("item=" + item + " is error!");
        Class<?> impClass = clazz;
        var sname = clazz.getSimpleName();
        if (sname.startsWith("I")) {
            var name = clazz.getName();
            var className = name.substring(0, name.length() - sname.length()) + sname.substring(1);
            impClass = Class.forName(className);
        }
        if (impClass == null)
            throw new Exception("clazz(" + clazz.getName() + ") is error!");
        T cache = null;
        if (arr.length == 1) {
            var c = impClass.getConstructor(String.class, RedisConnection.class, ICacheKey.class, String.class,
                    Class.class, Class.class);
            if (c == null)
                throw new Exception("clazz(" + clazz.getName() + ") is error!");
            cache = (T) c.newInstance(item, getConnection(), getCacheKey(), getPrefix(), fieldClass, valueClass);
        } else {
            var c = impClass.getConstructor(String.class, String.class, RedisConnection.class, ICacheKey.class,
                    String.class, Class.class, Class.class);
            if (c == null)
                throw new Exception("clazz(" + clazz.getName() + ") is error!");
            cache = (T) c.newInstance(arr[0], arr[1], getConnection(), getCacheKey(), getPrefix(), fieldClass,
                    valueClass);
        }

        return cache;
    }

    @Override
    public void close() throws Exception {
        this.connectionFactory = null;
        this.cacheKey = null;
        this.prefix = null;
        this.mapper = null;
    }
}
