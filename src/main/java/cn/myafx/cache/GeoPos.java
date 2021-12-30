package cn.myafx.cache;

import org.springframework.data.geo.Point;

/**
 * gps坐标
 */
public final class GeoPos {
    /**
     * 经度
     */
    public final double Lon;
    /**
     * 纬度
     */
    public final double Lat;

    /**
     * GeoPos
     * @param lon 经度
     * @param lat 纬度
     * @throws Exception lat &lt; -90 || lat &gt; 90, lon &lt; -180 || lon &gt; 180
     */
    public GeoPos(double lon, double lat) throws Exception {
        if(lat < -90 || lat > 90) throw new Exception("lat = " + lat + " is error!");
        if(lon < -180 || lon > 180) throw new Exception("lon = " + lon + " is error!");
        
        this.Lat = lat;
        this.Lon = lon;
    }

    public double toX(){
        return this.Lon;
    }

    public double toY(){
        return this.Lat;
    }

    public Point toPoint(){
        return new Point(this.toX(), this.toY());
    }

    public static Point toPoint(double lon, double lat)throws Exception {
        if(lat < -90 || lat > 90) throw new Exception("lat = " + lat + " is error!");
        if(lon < -180 || lon > 180) throw new Exception("lon = " + lon + " is error!");
        
        return new Point(lon, lat);
    }

    public static GeoPos toGeoPos(Point point)throws Exception {
        if(point == null) return null;

        return new GeoPos(point.getX(), point.getY());
    }
}
