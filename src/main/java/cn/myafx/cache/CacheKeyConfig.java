package cn.myafx.cache;

import java.util.List;

/**
 * 缓存key配置
 */
public final class CacheKeyConfig {
    /**
     * db节点
     */
    public final String Node;
    /**
     * 名称
     */
    public final String Item;
    /**
     * 配置key
     */
    public final String Key;
    /**
     * 过期时间, 秒
     */
    public final Integer Expire;
    /**
     * fen
     */
    public final List<Integer> Db;

    /**
     * CacheKeyModel
     * @param node db 节点名称
     * @param item 配置名称
     * @param key 配置key
     * @param expire 过期时间, 秒
     * @param db 分配db
     */
    public CacheKeyConfig(String node, String item, String key, Integer expire, List<Integer> db){
        this.Node = node;
        this.Key = key;
        this.Item = item;
        this.Expire = expire;
        this.Db = db;
    }
}
