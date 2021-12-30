package cn.myafx.cache.base;

import java.util.List;
import java.util.Map;

import cn.myafx.cache.ICacheCursor;

/**
 * hash 接口
 */
public interface IHashCache<TField, TValue> extends IRedisCache {
    /**
     * 添加或更新数据
     * @param field hash key
     * @param value hash value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    boolean set(TField field, TValue value, Object... args) throws Exception;

    /// <param name="args">缓存key参数</param>
    /**
     * 添加或更新数据
     * @param map Map
     * @param args 缓存key参数
     * @throws Exception
     */
    void addOrUpdate(Map<TField, TValue> map, Object... args) throws Exception;
    /**
     * 获取数据
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    Map<TField, TValue> get(Object... args) throws Exception;

    /**
     * 获取数据
     * @param field hash key
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    TValue getValue(TField field, Object... args) throws Exception;

    /**
     * 获取数据
     * @param fields hash key
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    List<TValue> getValue(List<TField> fields, Object... args) throws Exception;

    /**
     * 获取hash key
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    List<TField> geTFields( Object... args) throws Exception;

    /**
     * 获取hash value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    List<TValue> getValues(Object... args) throws Exception;

    /**
     * 获取hash key 数量
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long getCount(Object... args) throws Exception;

    /**
     * 是否存在hash key
     * @param field hash key
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    boolean exists(TField field, Object... args) throws Exception;

    /**
     * 移除hash key
     * @param field hash key
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    boolean delete(TField field, Object... args) throws Exception;

    /**
     * 移除hash key
     * @param fields hash key
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long delete(List<TField> fields, Object... args) throws Exception;

    /**
     * hash value 原子自增，TValue 必须是 long、int类型
     * @param field hash key
     * @param incrementValue 增量
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long increment(TField field, long incrementValue, Object... args) throws Exception;

    /**
     * hash value 原子自减，TValue 必须是 long、int类型
     * @param field hash key
     * @param decrementValue 自减量
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long decrement(TField field, long decrementValue, Object... args) throws Exception;

    /**
     * 游标方式读取数据
     * @param pattern 搜索表达式
     * @param count 游标页大小
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    ICacheCursor<Map.Entry<TField, TValue>> scan(String pattern, int count, Object... args) throws Exception;
}
