package cn.myafx.cache.base;

import java.util.List;

import cn.myafx.cache.ExcludeType;
import cn.myafx.cache.ICacheCursor;
import cn.myafx.cache.OpWhen;
import cn.myafx.cache.Sort;
import cn.myafx.cache.SortSetModel;

/**
 * 有序集合接口
 */
public interface ISortSetCache<T> extends IValueCache<T> {

    /**
     * 添加或更新数据
     * @param value value
     * @param score 排序分
     * @param when 操作类型
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    boolean addOrUpdate(T value, double score, OpWhen when, Object... args) throws Exception;

    /**
     * 添加或更新数据
     * @param m SortSetModel
     * @param when 操作类型
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    boolean addOrUpdate(SortSetModel<T> m, OpWhen when, Object... args) throws Exception;

    /**
     * 添加或更新数据
     * @param list SortSetModel List
     * @param when 操作类型
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long addOrUpdate(List<SortSetModel<T>> list, OpWhen when, Object... args) throws Exception;

    /**
     * 减少 score
     * @param value value
     * @param score 排序分
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    double decdrement(T value, double score, Object... args) throws Exception;

    /**
     * 增加 score
     * @param value  value
     * @param score 排序分
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    double increment(T value, double score, Object... args) throws Exception;

    /**
     * 获取集合数量
     * @param minScore 最小排序分
     * @param maxScore 最大排序分
     * @param excType 条件类型
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long getCount(double minScore, double maxScore, ExcludeType excType, Object... args) throws Exception;

    /**
     * 返回并集合
     * @param sort 排序
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    SortSetModel<T> pop(Sort sort, Object... args) throws Exception;

    /**
     * 返回并集合
     * @param count 返回数量
     * @param sort 排序
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    List<SortSetModel<T>> pop(long count, Sort sort, Object... args) throws Exception;

    /**
     * 获取集合
     * @param start 开始位置
     * @param stop 结束位置
     * @param sort 排序
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    List<T> get(long start, long stop, Sort sort, Object... args) throws Exception;

    /**
     * 获取集合
     * @param start 开始位置
     * @param stop 结束位置
     * @param sort 排序
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    List<SortSetModel<T>> getWithScores(long start, long stop, Sort sort, Object... args) throws Exception;

    /**
     * 获取集合
     * @param startScore 开始位置排序分
     * @param stopScore 结束位置排序分
     * @param excType 条件类型
     * @param sort 排序
     * @param skip 跳过多少个
     * @param take 返回多少个
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    List<T> getByScore(double startScore, double stopScore, ExcludeType excType, Sort sort, int skip, int take, Object... args) throws Exception;

    /**
     * 获取集合
     * @param startScore 开始位置排序分
     * @param stopScore 结束位置排序分
     * @param excType 条件类型
     * @param sort 排序
     * @param skip 跳过多少个
     * @param take 返回多少个，-1.返回所有
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    List<SortSetModel<T>> getByScoreWithScores(double startScore, double stopScore, ExcludeType excType, Sort sort, int skip, int take, Object... args) throws Exception;

    /**
     * 移除集合
     * @param value value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    boolean delete(T value, Object... args) throws Exception;

    /**
     * 移除集合
     * @param list value List
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long delete(List<T> list, Object... args) throws Exception;

    /**
     * 移除集合
     * @param start 开始位置
     * @param stop 结束位置
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long delete(long start, long stop, Object... args) throws Exception;

    /**
     * 移除集合
     * @param startScore 开始位置排序分
     * @param stopScore 结束位置排序分
     * @param excType 条件类型
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    long deleteByScore(double startScore, double stopScore, ExcludeType excType, Object... args) throws Exception;

    /**
     * 游标方式读取数据
     * @param pattern 搜索表达式
     * @param pageSize 游标页大小
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    ICacheCursor<SortSetModel<T>> scan(String pattern, int pageSize, Object... args) throws Exception;
}
