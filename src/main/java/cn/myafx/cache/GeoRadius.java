package cn.myafx.cache;

/**
 * 位置半径信息
 */
public final class GeoRadius {
    /**
     * 位置点名称
     */
    public final String Name;
    /**
     * 距离
     */
    public final Double Distance;

    /**
     * gps坐标
     */
    public final GeoPos Position;

    /**
     * GeoRadius
     * @param name 位置点名称
     * @param distance 距离
     * @param position gps坐标
     */
    public GeoRadius(String name, Double distance, GeoPos position) {
        this.Name = name;
        this.Distance = distance;
        this.Position = position;
    }
}
