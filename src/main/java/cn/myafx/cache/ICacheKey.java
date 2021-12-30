package cn.myafx.cache;

import java.util.List;

/**
 * 缓存key配置接口
 */
public interface ICacheKey {
    /**
     * get
     * @param node db节点
     * @param item 节点名称
     * @return
     */
    CacheKeyConfig get(String node, String item);
    /**
     * 获取key
     * @param node 节点
     * @param item 名称
     * @return key
     */
    String getKey(String node, String item);

    /**
     * 获取过期时间, 秒
     * @param node 节点
     * @param item 名称
     * @return 过期时间, 秒
     */
    Integer getExpire(String node, String item);

    /**
     * 获取db
     * @param node 节点
     * @param item 名称
     * @return db list
     */
    List<Integer> getDb(String node, String item);
}
