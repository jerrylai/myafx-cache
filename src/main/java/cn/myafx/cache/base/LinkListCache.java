package cn.myafx.cache.base;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisListCommands.Position;

import cn.myafx.cache.ICacheKey;

/**
 * 
 */
public class LinkListCache<T> extends RedisCache implements ILinkListCache<T> {
    private Class<T> clazz;

    /**
     * LinkListCache
     * 
     * @param node            缓存key配置db节点
     * @param item            缓存key配置项
     * @param redisConnection RedisConnection
     * @param cacheKey        ICacheKey
     * @param prefix          缓存前缀
     * 
     * @throws Exception
     */
    public LinkListCache(String node, String item, RedisConnection redisConnection, ICacheKey cacheKey, String prefix,
            Class<T> clazz) throws Exception {
        super(node, item, redisConnection, cacheKey, prefix);
        if (clazz == null)
            throw new Exception("clazz is null!");
        this.clazz = clazz;
    }

    /**
     * 添加到左边第一个
     * 
     * @param value value
     * @param args  缓存key参数
     * @return
     */
    @Override
    public long pushLeft(T value, Object... args) throws Exception {
        if (value == null)
            throw new Exception("value is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.listCommands().lPush(cachekeyBytes, serialize(value));

        return r == null ? -1 : r;
    }

    /**
     * 添加到左边第一个
     * 
     * @param list value list
     * @param args 缓存key参数
     * @return
     */
    @Override
    public long pushLeft(List<T> list, Object... args) throws Exception {
        if (list == null)
            throw new Exception("list is null!");
        if (list.size() == 0)
            return 0;
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        byte[][] arr = new byte[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            var v = list.get(i);
            if (v == null)
                throw new Exception("list item is null!");
            arr[i] = serialize(v);
        }
        var r = this.redis.listCommands().lPush(cachekeyBytes, arr);

        return r == null ? -1 : r;
    }

    /**
     * 添加到右边第一个
     * 
     * @param value value
     * @param args  缓存key参数
     * @return
     */
    @Override
    public long pushRight(T value, Object... args) throws Exception {
        if (value == null)
            throw new Exception("value is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.listCommands().rPush(cachekeyBytes, serialize(value));

        return r == null ? -1 : r;
    }

    /**
     * 添加到右边第一个
     * 
     * @param list value list
     * @param args 缓存key参数
     * @return
     */
    @Override
    public long pushRight(List<T> list, Object... args) throws Exception {
        if (list == null)
            throw new Exception("list is null!");
        if (list.size() == 0)
            return 0;
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        byte[][] arr = new byte[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            var v = list.get(i);
            if (v == null)
                throw new Exception("list item is null!");
            arr[i] = serialize(v);
        }
        var r = this.redis.listCommands().rPush(cachekeyBytes, arr);

        return r == null ? -1 : r;
    }

    /**
     * 获取指定索引位置数据
     * 
     * @param index 索引位置
     * @param args  缓存key参数
     * @return
     */
    @Override
    public T get(long index, Object... args) throws Exception {
        if (index < 0)
            throw new Exception("index = " + index + " is error!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.listCommands().lIndex(cachekeyBytes, index);
        T m = deserialize(r, clazz);

        return m;
    }

    /**
     * 获取一个范围数据
     * 
     * @param start 开始位置
     * @param stop  结束位置，-1.全部
     * @param args  缓存key参数
     * @return
     */
    @Override
    public List<T> getRange(long start, long stop, Object... args) throws Exception {
        if (start < 0)
            throw new Exception("start = " + start + " is error!");
        if (stop != -1 && stop < start)
            throw new Exception("stop = " + stop + " is error!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.listCommands().lRange(cachekeyBytes, start, stop);
        List<T> list = null;
        if (r != null) {
            list = new ArrayList<>(r.size());
            for (var b : r) {
                list.add(deserialize(b, clazz));
            }
        }

        return list;
    }

    /**
     * 插入到那个value后面
     * 
     * @param pivot 要插入到那个value后面
     * @param value 插入value
     * @param args  缓存key参数
     * @return
     */
    @Override
    public long insertAfter(T pivot, T value, Object... args) throws Exception {
        if (pivot == null)
            throw new Exception("pivot is null!");
        if (value == null)
            throw new Exception("value is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.listCommands().lInsert(cachekeyBytes, Position.AFTER, serialize(pivot), serialize(value));

        return r == null ? -1 : r;
    }

    /**
     * 插入到那个value前面
     * 
     * @param pivot 要插入到那个value前面
     * @param value 插入value
     * @param args  缓存key参数
     * @return
     */
    @Override
    public long insertBefore(T pivot, T value, Object... args) throws Exception {
        if (pivot == null)
            throw new Exception("pivot is null!");
        if (value == null)
            throw new Exception("value is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.listCommands().lInsert(cachekeyBytes, Position.BEFORE, serialize(pivot), serialize(value));

        return r == null ? -1 : r;
    }

    /**
     * 返回并移除左边第一个
     * 
     * @param args 缓存key参数
     * @return
     */
    @Override
    public T popLeft(Object... args) throws Exception {
        if (clazz == null)
            throw new Exception("clazz is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.listCommands().lPop(cachekeyBytes);
        T m = deserialize(r, clazz);

        return m;
    }

    /**
     * 返回并移除右边第一个
     * 
     * @param args 缓存key参数
     * @return
     */
    @Override
    public T popRight(Object... args) throws Exception {
        if (clazz == null)
            throw new Exception("clazz is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.listCommands().rPop(cachekeyBytes);
        T m = deserialize(r, clazz);

        return m;
    }

    /**
     * 更新
     * 
     * @param index 位置
     * @param value 更新后value
     * @param args  缓存key参数
     * @return
     */
    @Override
    public boolean update(long index, T value, Object... args) throws Exception {
        if (index < 0)
            throw new Exception("index=" + index + " is error!");
        if (value == null)
            throw new Exception("value is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        this.redis.listCommands().lSet(cachekeyBytes, index, serialize(value));

        return true;
    }

    /**
     * 移除数据
     * 
     * @param value 要删除的value
     * @param count 匹配数据个数，0.匹配所有
     * @param args  缓存key参数
     * @return
     */
    @Override
    public long delete(T value, long count, Object... args) throws Exception {
        if (count < 0)
            throw new Exception("count=" + count + " is error!");
        if (value == null)
            throw new Exception("value is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.listCommands().lRem(cachekeyBytes, count, serialize(value));

        return r == null ? -1 : r;
    }

    /**
     * 移除指定区域之外的所有数据
     * 
     * @param start 开始位置
     * @param stop  结束位置
     * @param args  缓存key参数
     */
    @Override
    public void trim(long start, long stop, Object... args) throws Exception {
        if (start < 0)
            throw new Exception("start=" + start + " is error!");
        if (stop < start)
            throw new Exception("stop=" + stop + " is error!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        this.redis.listCommands().lTrim(cachekeyBytes, start, stop);
    }

    /**
     * 获取链表长度
     * 
     * @param args 缓存key参数
     * @return
     */
    @Override
    public long getCount(Object... args) throws Exception {
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.listCommands().lLen(cachekeyBytes);

        return r == null ? 0 : r;
    }
}
