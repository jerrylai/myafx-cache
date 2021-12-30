package cn.myafx.cache;

/**
 * 搜索最近坐标返回数据选项
 */
public final class RadiusOptions {
    /**
     * None
     */
    public final static int None = 0;
    /**
     * 返回结果会带上匹配位置的经纬度
     */
    public final static int WithCoordinates = 1;
    /**
     * 返回结果会带上匹配位置与给定地理位置的距离
     */
    public final static int WithDistance = 2;
}
