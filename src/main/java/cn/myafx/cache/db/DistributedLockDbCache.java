package cn.myafx.cache.db;

import org.springframework.data.redis.connection.RedisConnection;

import cn.myafx.cache.ICacheKey;
import cn.myafx.cache.base.StringCache;
/**
 * 分布式锁db接口
 */
public class DistributedLockDbCache<T> extends StringCache<T> implements IDistributedLockDbCache<T> {
    
    /**
     * StringCache
     * @param node 缓存key配置db节点
     * @param item 缓存key配置项
     * @param redisConnection RedisConnection
     * @param cacheKey ICacheKey
     * @param prefix 缓存前缀
     * @param clazz T.class
     * @throws Exception
     */
    public DistributedLockDbCache(String node, String item, RedisConnection redisConnection, ICacheKey cacheKey, String prefix, Class<T> clazz) throws Exception {
        super("DistributedLockDbCache", item, redisConnection, cacheKey, prefix, clazz);
    }

    
}
