package cn.myafx.cache.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands.Limit;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.data.redis.connection.RedisZSetCommands.ZAddArgs;
import org.springframework.data.redis.core.ScanOptions.ScanOptionsBuilder;

import cn.myafx.cache.ExcludeType;
import cn.myafx.cache.ICacheCursor;
import cn.myafx.cache.ICacheKey;
import cn.myafx.cache.OpWhen;
import cn.myafx.cache.Sort;
import cn.myafx.cache.SortSetModel;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;

/**
 * 有序集合
 */
public class SortSetCache<T> extends RedisCache implements ISortSetCache<T> {

    private Class<T> clazz;

    /**
     * SortSetCache
     * @param node 缓存key配置db节点
     * @param item 缓存key配置项
     * @param redisConnection RedisConnection
     * @param cacheKey ICacheKey
     * @param prefix 缓存前缀
     * @param clazz T.class
     * @throws Exception
     */
    public SortSetCache(String node, String item, RedisConnection redisConnection, ICacheKey cacheKey, String prefix, Class<T> clazz) throws Exception {
        super(node, item, redisConnection, cacheKey, prefix);
        if(clazz == null) throw new Exception("clazz is null!");
        this.clazz = clazz;
    }


    private ZAddArgs toZAddArgs(OpWhen when){
        switch(when){
            case Exists:
                return ZAddArgs.ifExists();
            case NotExists:
                return ZAddArgs.ifNotExists();
            case Always:
            default:
                return ZAddArgs.empty();
        }
    }

    /**
     * 添加或更新数据
     * @param value value
     * @param score 排序分
     * @param when 操作类型
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public boolean addOrUpdate(T value, double score, OpWhen when, Object... args) throws Exception{
        if (value == null) throw new Exception("value is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.zAdd(cacheKeyBytes, score, serialize(value), toZAddArgs(when));

        return r == null ? false : true;
    }

    /**
     * 添加或更新数据
     * @param m SortSetModel
     * @param when 操作类型
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public boolean addOrUpdate(SortSetModel<T> m, OpWhen when, Object... args) throws Exception{
        if (m == null) throw new Exception("m is null!");
        if (m.Value == null) throw new Exception("value is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.zAdd(cacheKeyBytes, m.Score, serialize(m.Value), toZAddArgs(when));

        return r == null ? false : true;
    }

    /**
     * 添加或更新数据
     * @param list SortSetModel List
     * @param when 操作类型
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public long addOrUpdate(List<SortSetModel<T>> list, OpWhen when, Object... args) throws Exception{
        if (list == null) throw new Exception("list is null!");
        if (list.size() == 0) return 0;
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        Set<Tuple> hashset = new HashSet<>(list.size());
        for(var sm : list){
            if(sm == null)throw new Exception("list item is null!");
            if(sm.Value == null)throw new Exception("list item.value is null!");
            hashset.add(new SortSetTuple(serialize(sm.Value), sm.Score));
        }
        var r = this.redis.zAdd(cacheKeyBytes, hashset, toZAddArgs(when));

        return r == null ? 0 : r;
    }

    /**
     * 减少 score
     * @param value value
     * @param score 排序分
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public double decdrement(T value, double score, Object... args) throws Exception{
        if (value == null) throw new Exception("value is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.zIncrBy(cacheKeyBytes, -score, serialize(value));

        return r == null ? 0 : r;
    }

    /**
     * 增加 score
     * @param value  value
     * @param score 排序分
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public double increment(T value, double score, Object... args) throws Exception{
        if (value == null) throw new Exception("value is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.zIncrBy(cacheKeyBytes, score, serialize(value));

        return r == null ? 0 : r;
    }
    /**
     * 获取集合数量
     * @param minScore 最小排序分
     * @param maxScore 最大排序分
     * @param excType 条件类型
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public long getCount(double minScore, double maxScore, ExcludeType excType, Object... args) throws Exception{
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.zCount(cacheKeyBytes, minScore, maxScore);

        return r == null ? 0 : r;
    }

    /**
     * 返回集合
     * @param sort 排序
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public SortSetModel<T> pop(Sort sort, Object... args) throws Exception{
        if (clazz == null) throw new Exception("clazz is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        Tuple r = null;
        if(sort == Sort.Asc) r = this.redis.zPopMax(cacheKeyBytes);
        else r = this.redis.zPopMin(cacheKeyBytes);
        SortSetModel<T> m = null;
        if(r != null){
            m = new SortSetModel<T>(deserialize(r.getValue(), clazz), r.getScore());
        }

        return m;
    }

    /**
     * 返回并集合
     * @param count 返回数量
     * @param sort 排序
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public List<SortSetModel<T>> pop(long count, Sort sort, Object... args) throws Exception{
        if (clazz == null) throw new Exception("clazz is null!");
        if (count <= 0) throw new Exception("count="+count+" is error!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        Set<Tuple> r = null;
        if(sort == Sort.Asc) r = this.redis.zPopMax(cacheKeyBytes, count);
        else r = this.redis.zPopMin(cacheKeyBytes, count);
        List<SortSetModel<T>> list = null;
        if(r != null){
            list = new ArrayList<>(r.size());
            for (var rt : r) {
                list.add(new SortSetModel<T>(deserialize(rt.getValue(), clazz), rt.getScore()));
            }
        }

        return list;
    }

    /**
     * 获取集合
     * @param start 开始位置
     * @param stop 结束位置
     * @param sort 排序
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public List<T> get(long start, long stop, Sort sort, Object... args) throws Exception{
        if (clazz == null) throw new Exception("clazz is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        Set<byte[]> r = null;
        if(sort == Sort.Asc) r = this.redis.zRange(cacheKeyBytes, start, stop);
        else r = this.redis.zRevRange(cacheKeyBytes, start, stop);
        List<T> list = null;
        if(r != null){
            list = new ArrayList<>(r.size());
            for (var rt : r) {
                list.add(deserialize(rt, clazz));
            }
        }

        return list;
    }

    /**
     * 获取集合
     * @param start 开始位置
     * @param stop 结束位置
     * @param sort 排序
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public List<SortSetModel<T>> getWithScores(long start, long stop, Sort sort, Object... args) throws Exception{
        if (clazz == null) throw new Exception("clazz is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        Set<Tuple> r = null;
        if(sort == Sort.Asc) r = this.redis.zRangeWithScores(cacheKeyBytes, start, stop);
        else r = this.redis.zRevRangeWithScores(cacheKeyBytes, start, stop);
        List<SortSetModel<T>> list = null;
        if(r != null){
            list = new ArrayList<>(r.size());
            for (var rt : r) {
                list.add(new SortSetModel<T>(deserialize(rt.getValue(), clazz), rt.getScore()));
            }
        }

        return list;
    }

    private Range toRange(double startScore, double stopScore, ExcludeType excType){
        Range range = Range.range();
        switch(excType){
            case Start:
                range.gt(startScore);
                range.lte(stopScore);
                break;
            case Stop:
                range.gte(startScore);
                range.lt(stopScore);
                break;
            case Both:
                range.gt(startScore);
                range.lt(stopScore);
                break;
            case None:
            default:
                range.gte(startScore);
                range.lte(stopScore);
                break;
        }

        return range;
    }

    /**
     * 获取集合
     * @param startScore 开始位置排序分
     * @param stopScore 结束位置排序分
     * @param excType 条件类型
     * @param sort 排序
     * @param skip 跳过多少个
     * @param take 返回多少个, -1.返回所有
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public List<T> getByScore(double startScore, double stopScore, ExcludeType excType, Sort sort, int skip, int take, Object... args) throws Exception{
        if (clazz == null) throw new Exception("clazz is null!");
        if (skip < 0) throw new Exception("skip="+skip+" is error!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var range = toRange(startScore, stopScore, excType);
        var limit = Limit.limit().offset(skip).count(take);
        Set<byte[]> r = null;
        if(sort == Sort.Asc) r = this.redis.zRangeByLex(cacheKeyBytes, range, limit);
        else r = this.redis.zRevRangeByLex(cacheKeyBytes, range, limit);
        List<T> list = null;
        if(r != null){
            list = new ArrayList<>(r.size());
            for (var rt : r) {
                list.add(deserialize(rt, clazz));
            }
        }

        return list;
    }

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
    @Override
    public List<SortSetModel<T>> getByScoreWithScores(double startScore, double stopScore, ExcludeType excType, Sort sort, int skip, int take, Object... args) throws Exception{
        if (clazz == null) throw new Exception("clazz is null!");
        if (skip < 0) throw new Exception("skip="+skip+" is error!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var range = toRange(startScore, stopScore, excType);
        var limit = Limit.limit().offset(skip).count(take);
        Set<Tuple> r = null;
        if(sort == Sort.Asc) r = this.redis.zRangeByScoreWithScores(cacheKeyBytes, range, limit);
        else r = this.redis.zRevRangeByScoreWithScores(cacheKeyBytes, range, limit);
        List<SortSetModel<T>> list = null;
        if(r != null){
            list = new ArrayList<>(r.size());
            for (var rt : r) {
                list.add(new SortSetModel<T>(deserialize(rt.getValue(), clazz), rt.getScore()));
            }
        }

        return list;
    }

    /**
     * 移除集合
     * @param value value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public boolean delete(T value, Object... args) throws Exception{
        if (value == null) throw new Exception("value is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.zRem(cacheKeyBytes, serialize(value));

        return r > 0;
    }

    /**
     * 移除集合
     * @param list value List
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public long delete(List<T> list, Object... args) throws Exception{
        if (list == null) throw new Exception("list is null!");
        if(list.size() == 0) return 0;
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        byte[][] arr = new byte[list.size()][];
        for(var i=0; i<list.size(); i++){
            var m = list.get(i);
            if(m == null)throw new Exception("list item is null!");
            arr[i] = serialize(m);
        }
        var r = this.redis.zRem(cacheKeyBytes, arr);

        return r  == null ? 0 : r;
    }

    /**
     * 移除集合
     * @param start 开始位置
     * @param stop 结束位置
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public long delete(long start, long stop, Object... args) throws Exception{
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.zRemRange(cacheKeyBytes, start, stop);

        return r  == null ? 0 : r;
    }

    /**
     * 移除集合
     * @param startScore 开始位置排序分
     * @param stopScore 结束位置排序分
     * @param excType 条件类型
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public long deleteByScore(double startScore, double stopScore, ExcludeType excType, Object... args) throws Exception{
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.zRemRangeByScore(cacheKeyBytes, toRange(startScore, stopScore, excType));

        return r  == null ? 0 : r;
    }

    /**
     * 游标方式读取数据
     * @param pattern 搜索表达式
     * @param pageSize 游标页大小
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public ICacheCursor<SortSetModel<T>> scan(String pattern, int pageSize, Object... args) throws Exception{
        if(pattern == null || pattern.isEmpty()) throw new Exception("pattern is null!");
        if(pageSize <= 0)throw new Exception("pageSize="+pageSize+" is error!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        ScanOptionsBuilder opbu = ScanOptions.scanOptions().match(pattern).count(pageSize);
        var r = this.redis.zScan(cacheKeyBytes, opbu.build());

        return new SortSetCursor(r, clazz);
    }

    public class SortSetTuple implements Tuple{
        private byte[] value;
        private Double score;

        public SortSetTuple(byte[] value, Double score){
            this.value = value;
            this.score = score;
        }

        @Override
        public int compareTo(Double arg0) {
            return (int)(this.score - arg0);
        }

        @Override
        public byte[] getValue() {
            return this.value;
        }

        @Override
        public Double getScore() {
            return this.score;
        } 
    }

    public class SortSetCursor  implements ICacheCursor<SortSetModel<T>>{

        private Cursor<Tuple> cursor;
        private Class<T> valueClazz;

        public SortSetCursor(Cursor<Tuple> cursor, Class<T> valueClazz){
            this.cursor = cursor;
            this.valueClazz = valueClazz;
        }

        @Override
        public void close() {
            if(this.cursor != null){
                this.cursor.close();
                this.cursor = null;
                this.valueClazz = null;
            }
        }

        @Override
        public boolean hasNext() {
            return this.cursor.hasNext();
        }

        @Override
        public SortSetModel<T> next() {
            var tp = this.cursor.next();
            try{
                T v = deserialize(tp.getValue(), valueClazz);
                return new SortSetModel<T>(v, tp.getScore());
            }
            catch(Exception ex){}

            return null;
        }
    }
}
