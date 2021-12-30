package cn.myafx.cache.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;

import cn.myafx.cache.ICacheCursor;
import cn.myafx.cache.ICacheKey;

public class HashCache<TField, TValue> extends RedisCache implements IHashCache<TField, TValue> {
    private Class<TField> fieldClass;
    private Class<TValue> valueClass;
    /**
     * HashCache
     * @param node 缓存key配置db节点
     * @param item 缓存key配置项
     * @param redisConnection RedisConnection
     * @param cacheKey ICacheKey
     * @param prefix 缓存前缀
     * @param fieldClass TField.class
     * @param valueClass TValue.class
     * @throws Exception
     */
    public HashCache(String node, String item, RedisConnection redisConnection, ICacheKey cacheKey, String prefix, Class<TField> fieldClass, Class<TValue> valueClass) throws Exception{
        super(node, item, redisConnection, cacheKey, prefix);
        if(fieldClass == null) throw new Exception("fieldClass is null!");
        if(valueClass == null) throw new Exception("valueClass is null!");
        this.fieldClass = fieldClass;
        this.valueClass = valueClass;
    }
    /**
     * 添加或更新数据
     * @param field hash key
     * @param value hash value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public boolean set(TField field, TValue value, Object... args) throws Exception {
        if (field == null) throw new Exception("field is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.hSet(cachekeyBytes, serialize(field), serialize(value));

        return r;
    }

    /// <param name="args">缓存key参数</param>
    /**
     * 添加或更新数据
     * @param map Map
     * @param args 缓存key参数
     * @throws Exception
     */
    @Override
    public void addOrUpdate(Map<TField, TValue> map, Object... args) throws Exception {
        if (map == null) throw new Exception("map is null!");
        var hmap = new HashMap<byte[], byte[]>(map.size());
        List<byte[]> dels = new ArrayList<>();
        for(var h : map.entrySet()){
            if(h.getKey() == null) throw new Exception("map.key is null!");
            if(h.getValue() == null) dels.add(serialize(h.getKey()));
            else hmap.put(serialize(h.getValue()), serialize(h.getValue()));
        }
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        this.redis.hMSet(cachekeyBytes, hmap);
        if(dels.size() > 0){
            var delarr = new byte[dels.size()][];
            for(var i=0; i<dels.size(); i++) delarr[i] = dels.get(i);
            this.redis.hDel(cachekeyBytes, delarr);
        }
    }
    /**
     * 获取数据
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public Map<TField, TValue> get(Object... args)throws Exception {
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var rmap = this.redis.hGetAll(cachekeyBytes);
        Map<TField, TValue> map = null;
        if(rmap != null){
            map = new HashMap<>(rmap.size());
            for(var hm : rmap.entrySet()){
                map.put(deserialize(hm.getKey(), fieldClass), deserialize(hm.getValue(), valueClass));
            }
        }
        return map;
    }

    /**
     * 获取数据
     * @param field hash key
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public TValue getValue(TField field, Object... args) throws Exception{
        if(field == null) throw new Exception("field is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.hGet(cachekeyBytes, serialize(field));
        var v = deserialize(r, valueClass);

        return v;
    }

    /**
     * 获取数据
     * @param fields hash key
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public List<TValue> getValue(List<TField> fields, Object... args) throws Exception{
        if(fields == null) throw new Exception("fields is null!");
        if(fields.size() == 0) return new ArrayList<TValue>(0);
        byte[][] karr = new byte[fields.size()][];
        for(int i=0; i<fields.size(); i++){
            var k = fields.get(i);
            if(k == null) throw new Exception("fields item is null!");
            karr[i] = serialize(k);
        }
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.hMGet(cachekeyBytes, karr);
        List<TValue> list = null;
        if(r!= null){
            list = new ArrayList<>(r.size());
            for(var b : r){
                list.add(deserialize(b, valueClass));
            }
        }

        return list;
    }

    /**
     * 获取hash key
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public List<TField> geTFields(Object... args) throws Exception{
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.hKeys(cachekeyBytes);
        List<TField> list = null;
        if(r!= null){
            list = new ArrayList<>(r.size());
            for(var b : r){
                list.add(deserialize(b, fieldClass));
            }
        }

        return list;
    }

    /**
     * 获取hash value
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public List<TValue> getValues(Object... args) throws Exception{
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.hVals(cachekeyBytes);
        List<TValue> list = null;
        if(r!= null){
            list = new ArrayList<>(r.size());
            for(var b : r){
                list.add(deserialize(b, valueClass));
            }
        }

        return list;
    }

    /**
     * 获取hash key 数量
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public long getCount(Object... args) throws Exception{
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.hLen(cachekeyBytes);

        return r == null ? 0 : r;
    }

    /**
     * 是否存在hash key
     * @param field hash key
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public boolean exists(TField field, Object... args) throws Exception{
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.hExists(cachekeyBytes, serialize(field));

        return r == null ? false : r;
    }

    /**
     * 移除hash key
     * @param field hash key
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public boolean delete(TField field, Object... args) throws Exception{
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.hDel(cachekeyBytes, serialize(field));

        return r > 0;
    }

    /**
     * 移除hash key
     * @param fields hash key
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public long delete(List<TField> fields, Object... args) throws Exception{
        if(fields == null) throw new Exception("fields is null!");
        if(fields.size() == 0) return 0;
        byte[][] karr = new byte[fields.size()][];
        for(int i=0; i<fields.size(); i++){
            var k = fields.get(i);
            if(k == null) throw new Exception("fields item is null!");
            karr[i] = serialize(k);
        }
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.hDel(cachekeyBytes, karr);

        return r == null ? 0 : r;
    }

    /**
     * hash value 原子自增，TValue 必须是 long、int类型
     * @param field hash key
     * @param incrementValue 增量
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public long increment(TField field, long incrementValue, Object... args) throws Exception{
        if(field == null) throw new Exception("field is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.hIncrBy(cachekeyBytes, serialize(field), incrementValue);

        return r == null ? 0 : r;
    }

    /**
     * hash value 原子自减，TValue 必须是 long、int类型
     * @param field hash key
     * @param decrementValue 自减量
     * @param args 缓存key参数
     * @return
     * @throws Exception
     */
    @Override
    public long decrement(TField field, long decrementValue, Object... args) throws Exception{
        if(field == null) throw new Exception("field is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var r = this.redis.hIncrBy(cachekeyBytes, serialize(field), -decrementValue);

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
    public ICacheCursor<Map.Entry<TField, TValue>> scan(String pattern, int count, Object... args) throws Exception{
        if(pattern == null) throw new Exception("pattern is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var cachekeyBytes = getBytes(cachekey);
        var op = ScanOptions.scanOptions().count(count).match(serialize(pattern)).build();
        var r = this.redis.hScan(cachekeyBytes, op);

        return new HashCursor(r, fieldClass, valueClass);
    }


    public class HEntry implements Entry<TField,TValue>{

        private TField key;
        private TValue value;
        public HEntry(TField key, TValue value){
            this.key = key;
            this.value = value;
        }

        @Override
        public TField getKey() {
            return this.key;
        }

        @Override
        public TValue getValue() {
            return this.value;
        }

        @Override
        public TValue setValue(TValue arg0) {
            var v = this.value;
            this.value = arg0;
            return v;
        }

    }

    public class HashCursor implements ICacheCursor<Map.Entry<TField, TValue>>{

        private Cursor<Entry<byte[], byte[]>> cursor;
        private Class<TField> keyClazz;
        private Class<TValue> valueClazz;

        public HashCursor(Cursor<Entry<byte[], byte[]>> cursor, Class<TField> keyClazz, Class<TValue> valueClazz){
            this.cursor = cursor;
            this.keyClazz = keyClazz;
            this.valueClazz = valueClazz;
        }

        @Override
        public void close() {
            if(this.cursor != null){
                this.cursor.close();
                this.cursor = null;
                this.keyClazz = null;
                this.valueClazz = null;
            }
        }

        @Override
        public boolean hasNext() {
            return this.cursor.hasNext();
        }

        @Override
        public Entry<TField, TValue> next() {
            var n = this.cursor.next();
            Entry<TField, TValue> hn = null;
                try{
                TField k = deserialize(n.getKey(), this.keyClazz);
                TValue v = deserialize(n.getValue(), valueClazz);
                hn = new HEntry(k, v);
            }
            catch(Exception ex){}

            return hn;
        }
    }
}
