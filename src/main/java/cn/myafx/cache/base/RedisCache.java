package cn.myafx.cache.base;

import org.springframework.data.redis.connection.RedisConnection;

import cn.myafx.cache.*;

/**
 * redis 缓存
 */
public class RedisCache extends BaseCache implements IRedisCache {
    public static IJsonMapper DefaultJsonMapper;
    // .setTimeZone(TimeZone.getTimeZone("GMT+8"));
    protected IJsonMapper mapper = DefaultJsonMapper;
    /**
     * redis
     */
    protected RedisConnection redis;
    /**
     * 缓存key配置
     */
    protected CacheKeyConfig keyConfig;
    /**
     * 缓存前缀
     */
    protected String prefix;
    /**
     * NodeName
     */
    protected String nodeName;

    /**
     * RedisCache
     * 
     * @param node            缓存key配置db节点
     * @param item            缓存key配置项
     * @param redisConnection RedisConnection
     * @param cacheKey        ICacheKey
     * @param prefix          缓存前缀
     * @throws Exception
     */
    public RedisCache(String node, String item, RedisConnection redisConnection, ICacheKey cacheKey, String prefix)
            throws Exception {
        if (DefaultJsonMapper == null)
            throw new Exception("RedisCache.DefaultJsonMapper is null !");
        if (node == null || node.isEmpty())
            throw new Exception("node is null !");
        if (item == null || item.isEmpty())
            throw new Exception("item is null !");
        if (redisConnection == null)
            throw new Exception("redis is null !");
        if (cacheKey == null)
            throw new Exception("cacheKey is null !");
        this.keyConfig = cacheKey.get(node, item);
        if (this.keyConfig == null)
            throw new Exception(node + "/" + item + " 未配置！");
        this.redis = redisConnection;
        this.prefix = prefix != null ? prefix : "";

        StringBuilder stringBuilder = new StringBuilder();
        for (var c : this.keyConfig.Node.toCharArray()) {
            if ('A' <= c && c <= 'Z') {
                if (stringBuilder.length() > 0)
                    stringBuilder.append("_");
                stringBuilder.append((char) (c + 32));
            } else {
                stringBuilder.append(c);
            }
        }
        stringBuilder.append(":");
        this.nodeName = stringBuilder.toString();
    }

    /**
     * set ObjectMapper
     */
    @Override
    public void setJsonMapper(IJsonMapper mapper) {
        if (mapper != null)
            this.mapper = mapper;
    }

    /**
     * getDefault
     * 
     * @param clazz clazz
     * @return clazz default value
     * @throws Exception
     */
    protected Object getDefault(Class<?> clazz) throws Exception {
        Object obj = null;
        if (clazz.isPrimitive()) {
            if (clazz == boolean.class)
                obj = false;
            else if (clazz == char.class)
                obj = '\0';
            else if (clazz == byte.class)
                obj = (byte) 0;
            else if (clazz == short.class)
                obj = (short) 0;
            else if (clazz == int.class)
                obj = 0;
            else if (clazz == long.class)
                obj = 0l;
            else if (clazz == float.class)
                obj = 0f;
            else if (clazz == double.class)
                obj = 0d;
        }
        return obj;
    }

    protected byte[] getBytes(String cachekey) throws Exception {
        if (cachekey == null)
            return null;

        return cachekey.getBytes("utf-8");//
    }

    protected String getString(byte[] buffer) throws Exception {
        if (buffer == null || buffer.length == 0)
            return null;

        return new String(buffer, "utf-8");
    }

    /**
     * Serialize
     * 
     * @param value Object
     * @return byte[]
     * @throws Exception
     */
    protected byte[] serialize(Object value) throws Exception {
        if (value == null)
            return null;
        if (value instanceof byte[] buf)
            return buf;

        var json = this.mapper.serialize(value);

        return getBytes(json);
    }

    /**
     * Deserialize
     * 
     * @param <T>    T
     * @param buffer buffer
     * @param clazz  clazz
     * @return T
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected <T> T deserialize(byte[] buffer, Class<T> clazz) throws Exception {
        if (clazz == null)
            throw new Exception("clazz is null!");
        if (clazz == byte[].class) {
            Object obj = buffer;
            return (T) obj;
        }
        var json = getString(buffer);
        if (clazz == String.class) {
            Object obj = json;
            return (T) obj;
        }
        if (json == null || json.isEmpty()) {
            return (T) getDefault(clazz);
        }
        var m = this.mapper.deserialize(json, clazz);

        return m;
    }

    /**
     * 缓存key配置
     * 
     * @return
     */
    @Override
    public CacheKeyConfig getKeyConfig() {
        return this.keyConfig;
    }

    /**
     * 获取完整缓存key
     * 
     * @param args 缓存key参数
     * @return key
     */
    @Override
    public String getCacheKey(Object[] args) throws Exception {
        if (this.keyConfig.Key == null || this.keyConfig.Key.isEmpty())
            throw new Exception(
                    "cache key(Node=" + this.keyConfig.Node + ", Item=" + this.keyConfig.Item + ") is null!");
        var key = this.keyConfig.Key;
        if (args != null && args.length > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                var o = args[i];
                if (o instanceof Enum<?> e)
                    o = e.ordinal();
                stringBuilder.append(":" + (o == null ? "null" : o.toString().toLowerCase()));
            }
            key = key + stringBuilder.toString();
        }

        return this.prefix + this.nodeName + key;
    }

    /**
     * 获取完整key所在db
     * 
     * @param cachekey 完整缓存key
     * @return
     */
    @Override
    public int getCacheDb(String cachekey) {
        var list = this.keyConfig.Db;
        if (list == null || list.size() == 0)
            return 0;
        if (list.size() == 1)
            return list.get(0);
        int hash = 0;
        for (var c : cachekey.toCharArray()) {
            hash += c;
            if (hash > 255)
                hash = hash % 255;
        }
        var db = list.get(hash % list.size());

        return db == null ? 0 : db;
    }

    /**
     * 移除缓存
     * 
     * @param args 缓存key参数
     * @return
     */
    @Override
    public boolean remove(Object[] args) throws Exception {
        String key = this.getCacheKey(args);
        int db = this.getCacheDb(key);

        this.redis.select(db);
        var r = this.redis.keyCommands().del(getBytes(key));
        return r != null && r > 0;
    }

    /**
     * 缓存key是否存在
     * 
     * @param args 缓存key参数
     * @return
     */
    @Override
    public boolean contains(Object[] args) throws Exception {
        String key = this.getCacheKey(args);
        int db = this.getCacheDb(key);
        this.redis.select(db);

        var r = this.redis.keyCommands().exists(getBytes(key));
        return r == null ? false : r;
    }

    /**
     * 设置缓存有效时间
     * 
     * @param expireSeconds 缓存有效时间, 秒
     * @param args          缓存key参数
     * @return
     */
    @Override
    public boolean expire(Integer expireSeconds, Object[] args) throws Exception {
        String key = this.getCacheKey(args);
        int db = this.getCacheDb(key);
        this.redis.select(db);

        var r = expireSeconds != null && expireSeconds > 0
                ? this.redis.keyCommands().expire(getBytes(key), expireSeconds)
                : this.redis.keyCommands().persist(getBytes(key));

        return r == null ? false : r;
    }

    /**
     * 根据系统配置设置缓存有效时间
     * 
     * @param args 缓存key参数
     * @return
     */
    @Override
    public boolean expire(Object[] args) throws Exception {
        return this.expire(this.keyConfig.Expire, args);
    }

    /**
     * ping
     * 
     * @return
     */
    @Override
    public String ping() {
        return this.redis.ping();
    }

    /**
     * close
     */
    @Override
    public void close() throws Exception {
        this.keyConfig = null;
        this.nodeName = null;
        this.redis = null;
        this.prefix = null;
        this.mapper = null;
    }
}
