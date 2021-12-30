package cn.myafx.cache.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ScanOptions.ScanOptionsBuilder;

import cn.myafx.cache.ICacheCursor;
import cn.myafx.cache.ICacheKey;
import cn.myafx.cache.SetOp;

/**
 * set 集合
 */
public class SetCache<T> extends RedisCache implements ISetCache<T> {
    private Class<T> clazz;

    /**
     * SetCache
     * @param node 缓存key配置db节点
     * @param item 缓存key配置项
     * @param redisConnection RedisConnection
     * @param cacheKey ICacheKey
     * @param prefix 缓存前缀
     * @param clazz T.class
     * @throws Exception
     */
    public SetCache(String node, String item, RedisConnection redisConnection, ICacheKey cacheKey, String prefix, Class<T> clazz) throws Exception {
        super(node, item, redisConnection, cacheKey, prefix);
        if(clazz == null) throw new Exception("clazz is null!");
        this.clazz = clazz;
    }

    /**
     * 添加数据
     * @param value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public boolean add(T value, Object... args) throws Exception{
        if (value == null) throw new Exception("value is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.sAdd(cacheKeyBytes, serialize(value));

        return r > 0;
    }

    /**
     * 添加数据
     * @param list value list
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public long add(List<T> list, Object... args) throws Exception{
        if (list == null) throw new Exception("list is null!");
        if(list.size() ==0) return 0;
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        byte[][] arr = new byte[list.size()][];
        for(var i=0; i< list.size(); i++){
            var m = list.get(i);
            if(m == null)throw new Exception("list item is null!");
            arr[i] = serialize(m);
        }
        var r = this.redis.sAdd(cacheKeyBytes, arr);

        return r == null ? 0 : r;
    }

    /**
     * 获取集合
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public List<T> get(Object... args) throws Exception{
        if (clazz == null) throw new Exception("clazz is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.sMembers(cacheKeyBytes);
        List<T> list = null;
        if(r != null){
            list = new ArrayList<>(r.size());
            for(var b : r){
                list.add(deserialize(b, clazz));
            }
        }

        return list;
    }

    /**
     * 两个集合运算，返回运算结果
     * @param firstArgs 第一个集合缓存key参数
     * @param secondArgs 第二集合缓存key参数
     * @param op 操作
     * @return
     * @throws Exception
     */
    @Override
    public List<T> join(Object[] firstArgs, Object[] secondArgs, SetOp op) throws Exception{
        if (clazz == null) throw new Exception("clazz is null!");
        String firstCachekey = this.getCacheKey(firstArgs);
        String secondCachekey = this.getCacheKey(secondArgs);
        int db = this.getCacheDb(firstCachekey);
        this.redis.select(db);
        var firstCachekeyBytes = getBytes(firstCachekey);
        var secondCachekeyBytes = getBytes(secondCachekey);
        List<T> list = null;
        Set<byte[]> r = null;
        switch(op){
            case Union:
                r = this.redis.sUnion(firstCachekeyBytes, secondCachekeyBytes);
                break;
            case Intersect:
                r = this.redis.sInter(firstCachekeyBytes, secondCachekeyBytes);
                break;
            case Difference:
                r = this.redis.sDiff(firstCachekeyBytes, secondCachekeyBytes);
                break;
        }
        if(r != null){
            list = new ArrayList<>(r.size());
            for(var b : r){
                list.add(deserialize(b, clazz));
            }
        }
        
        return list;
    }

    /**
     * 两个集合运算，并将运算结果存储到新集合
     * @param addArgs 新集合缓存key参数
     * @param firstArgs 第一个集合缓存key参数
     * @param secondArgs 第二集合缓存key参数
     * @param op 操作
     * @return
     * @throws Exception
     */
    @Override
    public long joinAndAdd(Object[] addArgs, Object[] firstArgs, Object[] secondArgs, SetOp op) throws Exception{
        String addCachekey = this.getCacheKey(addArgs);
        String firstCachekey = this.getCacheKey(firstArgs);
        String secondCachekey = this.getCacheKey(secondArgs);
        int db = this.getCacheDb(firstCachekey);
        this.redis.select(db);
        var addCachekeyBytes = getBytes(addCachekey);
        var firstCachekeyBytes = getBytes(firstCachekey);
        var secondCachekeyBytes = getBytes(secondCachekey);
        Long r = null;
        switch(op){
            case Union:
                r = this.redis.sUnionStore(addCachekeyBytes, firstCachekeyBytes, secondCachekeyBytes);
                break;
            case Intersect:
                r = this.redis.sInterStore(addCachekeyBytes, firstCachekeyBytes, secondCachekeyBytes);
                break;
            case Difference:
                r = this.redis.sDiffStore(addCachekeyBytes, firstCachekeyBytes, secondCachekeyBytes);
                break;
        }
        
        return r == null ? 0 : r;
    }

    /**
     * value是否存在
     * @param value value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public boolean exist(T value, Object... args) throws Exception{
        if (value == null) throw new Exception("value is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.sIsMember(cacheKeyBytes, serialize(value));

        return r == null ? false : r;
    }

    /**
     * 集合数量
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public long getCount(Object... args) throws Exception{
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.sCard(cacheKeyBytes);

        return r == null ? 0 : r;
    }

    /**
     * 移动一个已存在对象到新集合
     * @param sourceArgs 源集合缓存key参数
     * @param desArgs 需要移到新集合缓存key参数
     * @param value 移动对象
     * @return
     * @throws Exception
     */
    @Override
    public boolean move(Object[] sourceArgs, Object[] desArgs, T value) throws Exception{
        String sourceCachekey = this.getCacheKey(sourceArgs);
        String desCachekey = this.getCacheKey(desArgs);
        int db = this.getCacheDb(sourceCachekey);
        this.redis.select(db);
        var sourceCachekeyBytes = getBytes(sourceCachekey);
        var desCachekeyBytes = getBytes(desCachekey);
        var r = this.redis.sMove(sourceCachekeyBytes, desCachekeyBytes, serialize(value));

        return r == null ? false : r;
    }
    
    /**
     * 返回并移除一个集合对象
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public T pop(Object... args) throws Exception{
        if (clazz == null) throw new Exception("clazz is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.sPop(cacheKeyBytes);
        T m = deserialize(r, clazz);

        return m;
    }

    /**
     * 返回并移除集合对象
     * @param count 数量
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public List<T> pop(int count, Object... args) throws Exception{
        if(count <= 0) throw new Exception("count="+count+" is error!");
        if (clazz == null) throw new Exception("clazz is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.sPop(cacheKeyBytes, count);
        List<T> list = null;
        if(r != null){
            list = new ArrayList<>(r.size());
            for(var b : r){
                list.add(deserialize(b, clazz));
            }
        }

        return list;
    }
    
    /**
     * 随机返回一个对象
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public T getRandomValue(Object... args) throws Exception{
        if (clazz == null) throw new Exception("clazz is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.sRandMember(cacheKeyBytes);
        T m = deserialize(r, clazz);

        return m;
    }
 
    /**
     * 随机返回对象
     * @param count 数量
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public List<T> getRandomValue(int count, Object... args) throws Exception{
        if(count <= 0) throw new Exception("count="+count+" is error!");
        if (clazz == null) throw new Exception("clazz is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.sRandMember(cacheKeyBytes, count);
        List<T> list = null;
        if(r != null){
            list = new ArrayList<>(r.size());
            for(var b : r){
                list.add(deserialize(b, clazz));
            }
        }

        return list;
    }

    /**
     * 移除对象
     * @param value value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public boolean delete(T value, Object... args)throws Exception{
        if (value == null) throw new Exception("value is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        var r = this.redis.sRem(cacheKeyBytes, serialize(value));

        return r > 0;
    }
 
    /**
     * 移除对象
     * @param list value list
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public long delete(List<T> list, Object... args)throws Exception{
        if (list == null) throw new Exception("list is null!");
        if(list.size() == 0) return 0;
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);
        byte[][] arr = new byte[list.size()][];
        for(var i=0; i< list.size(); i++){
            var m = list.get(i);
            if(m == null)throw new Exception("list item is null!");
            arr[i] = serialize(m);
        }
        var r = this.redis.sRem(cacheKeyBytes, arr);

        return r == null ? 0 : r;
    }

    /**
     * 游标方式读取数据
     * @param pattern 搜索表达式
     * @param count 游标页大小
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public ICacheCursor<T> scan(String pattern, int count, Object... args)throws Exception{
        if (pattern == null || pattern.isEmpty()) throw new Exception("pattern is null!");
        if(count <= 0) throw new Exception("count="+count+" is error!");
        if (clazz == null) throw new Exception("clazz is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cacheKeyBytes = getBytes(cachekey);

        ScanOptionsBuilder opbu = ScanOptions.scanOptions().count(count).match(getBytes(pattern));
        var r = this.redis.sScan(cacheKeyBytes, opbu.build());

        return new SetCursor(r, clazz);
    }

    public class SetCursor  implements ICacheCursor<T>{

        private Cursor<byte[]> cursor;
        private Class<T> valueClazz;

        public SetCursor(Cursor<byte[]> cursor, Class<T> valueClazz){
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
        public T next() {
            var buffer = this.cursor.next();
            try{
                T v = deserialize(buffer, valueClazz);
                return v;
            }
            catch(Exception ex){}

            return null;
        }
    }
}
