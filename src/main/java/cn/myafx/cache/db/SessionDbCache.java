package cn.myafx.cache.db;

import org.springframework.data.redis.connection.RedisConnection;

import cn.myafx.cache.ICacheKey;
import cn.myafx.cache.base.StringCache;

/**
 * session数据db
 */
public class SessionDbCache<T> extends StringCache<T> implements ISessionDbCache<T> {
    /**
     * session数据db
     * @param item 缓存item
     * @param redisConnection redis
     * @param cacheKey ICacheKey
     * @param prefix 缓存前缀
     * @param clazz T.class
     * @throws Exception
     */
    public SessionDbCache(String item, RedisConnection redisConnection, ICacheKey cacheKey, String prefix, Class<T> clazz) throws Exception{
        super("SessionDb", item, redisConnection, cacheKey, prefix, clazz);
    }
}
