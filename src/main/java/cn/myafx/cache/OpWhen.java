package cn.myafx.cache;

/**
 * 操作类型
 */
public enum OpWhen {
    /**
     * The operation should occur whether or not there is an existing value
     */
    Always,
    /**
     * The operation should only occur when there is an existing value
     */
    Exists,
    /**
     * The operation should only occur when there is not an existing value
     */
    NotExists,
}
