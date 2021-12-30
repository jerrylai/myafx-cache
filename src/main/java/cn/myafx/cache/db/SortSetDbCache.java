package cn.myafx.cache.db;

import org.springframework.data.redis.connection.RedisConnection;

import cn.myafx.cache.ICacheKey;
import cn.myafx.cache.base.SortSetCache;

/**
 * 有序集合db
 */
public class SortSetDbCache<T> extends SortSetCache<T> implements ISortSetDbCache<T> {
    /**
     * session数据db
     * @param item 缓存item
     * @param redisConnection redis
     * @param cacheKey ICacheKey
     * @param prefix 缓存前缀
     * @param clazz T.class
     * @throws Exception
     */
    public SortSetDbCache(String item, RedisConnection redisConnection, ICacheKey cacheKey, String prefix, Class<T> clazz) throws Exception{
        super("SortSetDb", item, redisConnection, cacheKey, prefix, clazz);
    }
}
