package cn.myafx.cache.base;

import cn.myafx.cache.OpWhen;

/**
 * string key value 接口
 */
public interface IStringCache<T> extends IValueCache<T> {

    /**
     * 获取缓存
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    T get(Object... args) throws Exception;

    /**
     * 添加或更新
     * @param m 缓存数据
     * @param when when
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    boolean set(T m, OpWhen when, Object... args) throws Exception;

    /**
     * 添加或更新
     * @param m 缓存数据
     * @param expireSeconds 缓存有效时间,秒
     * @param when 操作类型
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    boolean set(T m, int expireSeconds, OpWhen when, Object... args) throws Exception;

    /**
     * 原子增 T 必须是 int、 long
     * @param incrementValue 增量
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long increment(long incrementValue, Object... args) throws Exception;

    /**
     * 原子减 T 必须是 int、 long
     * @param decrementValue 减量
     * @param args 原子减 T 必须是 int、 long
     * @return
     * @throws Exception
     */
    long decrement(long decrementValue, Object... args) throws Exception;
}
