package cn.myafx.cache;

/**
 * 位置点坐标
 */
public final class GeoInfo {
    /// <summary>
    /// 位置点名称
    /// </summary>
    public final String Name;
    /// <summary>
    /// gps坐标
    /// </summary>
    public final GeoPos Position;

    /**
     * GeoInfo
     * @param name 位置点名称
     * @param position gps坐标
     */
    public GeoInfo(String name, GeoPos position){
        this.Name = name;
        this.Position = position;
    }
}
