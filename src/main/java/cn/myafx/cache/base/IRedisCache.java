package cn.myafx.cache.base;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.myafx.cache.CacheKeyConfig;

public interface IRedisCache extends IBaseCache {
    /**
     * setObjectMapper
     */
    void setObjectMapper(ObjectMapper mapper);
    /**
     * 缓存key配置
     * @return
     */
    CacheKeyConfig getKeyConfig();
    /**
     * 获取完整缓存key
     * @param args 缓存key参数
     * @return key
     * @throws Exception
     */
    String getCacheKey(Object[] args)throws Exception;
    /**
     * 获取完整key所在db
     * @param cachekey 完整缓存key
     * @return
     */
    int getCacheDb(String cachekey);
    /**
     * 缓存key是否存在
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    boolean contains(Object[] args)throws Exception;
    /**
     * 移除缓存
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    boolean remove(Object[] args)throws Exception;
    /**
     * 设置缓存有效时间
     * @param expireSeconds 缓存有效时间, 秒
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    boolean expire(Integer expireSeconds, Object[] args)throws Exception;
    /**
     * 根据系统配置设置缓存有效时间
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    boolean expire(Object[] args)throws Exception;
    /**
     * ping
     * @return
     */
    String ping();
}
