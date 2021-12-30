package cn.myafx.cache;

/**
 * 执行类型
 */
public enum ExcludeType {
    /**
     * Both start and stop are inclusive
     * start &lt;= value &lt;= stop
     */
    None,
    /**
     * Start is exclusive, stop is inclusive
     * start &lt; value &lt;= stop
     */
    Start,
    /**
     * Start is inclusive, stop is exclusive
     * start &lt;= value &lt; stop
     */
    Stop,
    /**
     * Both start and stop are exclusive
     * start &lt; value &lt; stop
     */
    Both
}
