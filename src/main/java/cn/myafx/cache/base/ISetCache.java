package cn.myafx.cache.base;

import java.util.List;

import cn.myafx.cache.ICacheCursor;
import cn.myafx.cache.SetOp;

/**
 * set 集合
 */
public interface ISetCache<T> extends IValueCache<T> {

    /**
     * 添加数据
     * @param value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    boolean add(T value, Object... args) throws Exception;

    /**
     * 添加数据
     * @param list value list
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long add(List<T> list, Object... args) throws Exception;

    /**
     * 获取集合
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    List<T> get(Object... args) throws Exception;

    /**
     * 两个集合运算，返回运算结果
     * @param firstArgs 第一个集合缓存key参数
     * @param secondArgs 第二集合缓存key参数
     * @param op 操作
     * @return
     * @throws Exception
     */
    List<T> join(Object[] firstArgs, Object[] secondArgs, SetOp op) throws Exception;

    /**
     * 两个集合运算，并将运算结果存储到新集合
     * @param addArgs 新集合缓存key参数
     * @param firstArgs 第一个集合缓存key参数
     * @param secondArgs 第二集合缓存key参数
     * @param op 操作
     * @return
     * @throws Exception
     */
    long joinAndAdd(Object[] addArgs, Object[] firstArgs, Object[] secondArgs, SetOp op) throws Exception;

    /**
     * value是否存在
     * @param value value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    boolean exist(T value, Object... args) throws Exception;

    /**
     * 集合数量
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long getCount(Object... args) throws Exception;

    /**
     * 移动一个已存在对象到新集合
     * @param sourceArgs 源集合缓存key参数
     * @param desArgs 需要移到新集合缓存key参数
     * @param value 移动对象
     * @return
     * @throws Exception
     */
    boolean move(Object[] sourceArgs, Object[] desArgs, T value) throws Exception;

    /**
     * 返回并移除一个集合对象
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    T pop(Object... args) throws Exception;

    /**
     * 返回并移除集合对象
     * @param count 数量
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    List<T> pop(int count, Object... args) throws Exception;
    
    /**
     * 随机返回一个对象
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    T getRandomValue(Object... args) throws Exception;
 
    /**
     * 随机返回对象
     * @param count 数量
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    List<T> getRandomValue(int count, Object... args) throws Exception;

    /**
     * 移除对象
     * @param value value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    boolean delete(T value, Object... args) throws Exception;
 
    /**
     * 移除对象
     * @param list value list
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long delete(List<T> list, Object... args) throws Exception;

    /**
     * 游标方式读取数据
     * @param pattern 搜索表达式
     * @param count 游标页大小
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    ICacheCursor<T> scan(String pattern, int count, Object... args) throws Exception;
}
