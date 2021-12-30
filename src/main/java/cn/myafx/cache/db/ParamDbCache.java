package cn.myafx.cache.db;

import org.springframework.data.redis.connection.RedisConnection;

import cn.myafx.cache.ICacheKey;
import cn.myafx.cache.base.StringCache;

/**
 * 常规带参数数据db
 */
public class ParamDbCache<T> extends StringCache<T> implements IParamDbCache<T> {
    /**
     * 常规带参数数据db
     * @param item 缓存item
     * @param redisConnection redis
     * @param cacheKey ICacheKey
     * @param prefix 缓存前缀
     * @param clazz T.class
     * @throws Exception
     */
    public ParamDbCache(String item, RedisConnection redisConnection, ICacheKey cacheKey, String prefix, Class<T> clazz) throws Exception{
        super("ParamDb", item, redisConnection, cacheKey, prefix, clazz);
    }
}
