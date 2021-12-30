package cn.myafx.cache.base;

import java.util.List;

/**
 * 链表接口
 */
public interface ILinkListCache<T> extends IValueCache<T> {

    /**
     * 添加到左边第一个
     * @param value value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long pushLeft(T value, Object... args)throws Exception;

    /**
     * 添加到左边第一个
     * @param list value list
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long pushLeft(List<T> list, Object... args)throws Exception;

    /**
     * 添加到右边第一个
     * @param value value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long pushRight(T value, Object... args)throws Exception;

    /**
     * 添加到右边第一个
     * @param list value list
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long pushRight(List<T> list, Object... args)throws Exception;

    /**
     * 获取指定索引位置数据
     * @param index 索引位置
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    T get(long index, Object... args)throws Exception;
    /**
     * 获取一个范围数据
     * @param start 开始位置
     * @param stop 结束位置，-1.全部
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    List<T> getRange(long start, long stop, Object... args)throws Exception;
    /**
     * 插入到那个value后面
     * @param pivot 要插入到那个value后面
     * @param value 插入value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long insertAfter(T pivot, T value, Object... args)throws Exception;
    /**
     * 插入到那个value前面
     * @param pivot 要插入到那个value前面
     * @param value 插入value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long insertBefore(T pivot, T value, Object... args)throws Exception;
    /**
     * 返回并移除左边第一个
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    T popLeft(Object... args)throws Exception;
    /**
     * 返回并移除右边第一个
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    T popRight(Object... args)throws Exception;

    /**
     * 更新
     * @param index 位置
     * @param value 更新后value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    boolean update(long index, T value, Object... args)throws Exception;

    /**
     * 移除数据
     * @param value 要删除的value
     * @param count 匹配数据个数，0.匹配所有
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long delete(T value, long count, Object... args)throws Exception;

    /**
     * 移除指定区域之外的所有数据
     * @param start 开始位置
     * @param stop 结束位置
     * @param args 缓存key参数
     * @throws Exception
     */
    void trim(long start, long stop, Object... args)throws Exception;
    /**
     * 获取链表长度
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long getCount(Object... args)throws Exception;
}
