package cn.myafx.cache.db;

import org.springframework.data.redis.connection.RedisConnection;

import cn.myafx.cache.ICacheKey;
import cn.myafx.cache.base.GeoCache;

/**
 * 
 */
public class GeoDbCache extends GeoCache implements IGeoDbCache {
    
     /**
     * 哈希db
     * @param item 缓存item
     * @param redisConnection redis
     * @param cacheKey ICacheKey
     * @param prefix 缓存前缀
     * @throws Exception
     */
    public GeoDbCache(String item, RedisConnection redisConnection, ICacheKey cacheKey, String prefix) throws Exception{
        super("GeoDb", item, redisConnection, cacheKey, prefix);
    }
}
