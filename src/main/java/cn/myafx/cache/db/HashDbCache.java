package cn.myafx.cache.db;

import org.springframework.data.redis.connection.RedisConnection;

import cn.myafx.cache.ICacheKey;
import cn.myafx.cache.base.HashCache;

/**
 * 
 */
public class HashDbCache<TField, TValue> extends HashCache<TField, TValue> implements IHashDbCache<TField, TValue> {
    
    /**
     * 哈希db
     * @param item 缓存item
     * @param redisConnection redis
     * @param cacheKey ICacheKey
     * @param prefix 缓存前缀
     * @param fieldClass TField.class
     * @param valueClass TValue.class
     * @throws Exception
     */
    public HashDbCache(String item, RedisConnection redisConnection, ICacheKey cacheKey, String prefix, Class<TField> fieldClass, Class<TValue> valueClass) throws Exception{
        super("HashDb", item, redisConnection, cacheKey, prefix,fieldClass,valueClass);
    }
}
