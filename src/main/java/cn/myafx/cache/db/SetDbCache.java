package cn.myafx.cache.db;

import org.springframework.data.redis.connection.RedisConnection;

import cn.myafx.cache.ICacheKey;
import cn.myafx.cache.base.SetCache;

/**
 * set集合db
 */
public class SetDbCache<T> extends SetCache<T> implements ISetDbCache<T> {
    /**
     * set集合db
     * @param item 缓存item
     * @param redisConnection redis
     * @param cacheKey ICacheKey
     * @param prefix 缓存前缀
     * @param clazz T.class
     * @throws Exception
     */
    public SetDbCache(String item, RedisConnection redisConnection, ICacheKey cacheKey, String prefix, Class<T> clazz) throws Exception{
        super("SetDb", item, redisConnection, cacheKey, prefix, clazz);
    }
}
