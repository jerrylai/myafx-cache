package cn.myafx.cache.db;

import org.springframework.data.redis.connection.RedisConnection;

import cn.myafx.cache.ICacheKey;
import cn.myafx.cache.base.LinkListCache;

/**
 * 链接db
 */
public class LinkListDbCache<T> extends LinkListCache<T> implements ILinkListDbCache<T> {
    
    /**
     * 链接db
     * @param item 缓存item
     * @param redisConnection redis
     * @param cacheKey ICacheKey
     * @param prefix 缓存前缀
     * @param clazz T.class
     * @throws Exception
     */
    public LinkListDbCache(String item, RedisConnection redisConnection, ICacheKey cacheKey, String prefix, Class<T> clazz) throws Exception{
        super("LinkListDb", item, redisConnection, cacheKey, prefix, clazz);
    }
}
