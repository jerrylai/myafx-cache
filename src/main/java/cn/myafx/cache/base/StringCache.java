package cn.myafx.cache.base;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.types.Expiration;

import cn.myafx.cache.ICacheKey;
import cn.myafx.cache.OpWhen;

public class StringCache<T> extends RedisCache implements IStringCache<T> {

    private Class<T> clazz;

    /**
     * StringCache
     * 
     * @param node            缓存key配置db节点
     * @param item            缓存key配置项
     * @param redisConnection RedisConnection
     * @param cacheKey        ICacheKey
     * @param prefix          缓存前缀
     * @param clazz           T.class
     * @throws Exception
     */
    public StringCache(String node, String item, RedisConnection redisConnection, ICacheKey cacheKey, String prefix,
            Class<T> clazz) throws Exception {
        super(node, item, redisConnection, cacheKey, prefix);
        if (clazz == null)
            throw new Exception("clazz is null!");
        this.clazz = clazz;
    }

    /**
     * 获取缓存
     * 
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public T get(Object... args) throws Exception {
        String caheKey = this.getCacheKey(args);
        int db = this.getCacheDb(caheKey);
        this.redis.select(db);
        var caheKeyBytes = getBytes(caheKey);
        var r = this.redis.stringCommands().get(caheKeyBytes);
        T m = deserialize(r, clazz);

        return m;
    }

    private SetOption toSetOption(OpWhen when) {
        switch (when) {
            case Exists:
                return SetOption.SET_IF_PRESENT;
            case NotExists:
                return SetOption.SET_IF_ABSENT;
            case Always:
            default:
                return SetOption.UPSERT;
        }
    }

    /**
     * 添加或更新
     * 
     * @param m    缓存数据
     * @param when when
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public boolean set(T m, OpWhen when, Object... args) throws Exception {
        String caheKey = this.getCacheKey(args);
        int db = this.getCacheDb(caheKey);
        this.redis.select(db);
        var caheKeyBytes = getBytes(caheKey);
        if (m == null) {
            this.redis.keyCommands().del(caheKeyBytes);
            return true;
        } else {
            var r = this.redis.stringCommands().set(caheKeyBytes, serialize(m), Expiration.persistent(),
                    toSetOption(when));

            return r == null ? false : r;
        }
    }

    /**
     * 添加或更新
     * 
     * @param m             缓存数据
     * @param expireSeconds 缓存有效时间,秒
     * @param when          操作类型
     * @param args          缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public boolean set(T m, int expireSeconds, OpWhen when, Object... args) throws Exception {
        String caheKey = this.getCacheKey(args);
        int db = this.getCacheDb(caheKey);
        this.redis.select(db);
        var caheKeyBytes = getBytes(caheKey);
        if (m == null) {
            this.redis.keyCommands().del(caheKeyBytes);
            return true;
        } else {
            var r = this.redis.stringCommands().set(caheKeyBytes, serialize(m), Expiration.seconds(expireSeconds),
                    toSetOption(when));

            return r == null ? false : r;
        }
    }

    /**
     * 原子增 T 必须是 int、 long
     * 
     * @param incrementValue 增量
     * @param args           缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public long increment(long incrementValue, Object... args) throws Exception {
        String caheKey = this.getCacheKey(args);
        int db = this.getCacheDb(caheKey);
        this.redis.select(db);
        var caheKeyBytes = getBytes(caheKey);
        var r = this.redis.stringCommands().incrBy(caheKeyBytes, incrementValue);

        return r == null ? 0 : r;
    }

    /**
     * 原子减 T 必须是 int、 long
     * 
     * @param decrementValue 减量
     * @param args           原子减 T 必须是 int、 long
     * @return
     * @throws Exception
     */
    @Override
    public long decrement(long decrementValue, Object... args) throws Exception {
        String caheKey = this.getCacheKey(args);
        int db = this.getCacheDb(caheKey);
        this.redis.select(db);
        var caheKeyBytes = getBytes(caheKey);
        var r = this.redis.stringCommands().incrBy(caheKeyBytes, -decrementValue);

        return r == null ? 0 : r;
    }
}
