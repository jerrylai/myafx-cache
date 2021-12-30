package cn.myafx.cache.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.domain.geo.Metrics;

import cn.myafx.cache.DistUnit;
import cn.myafx.cache.GeoInfo;
import cn.myafx.cache.GeoPos;
import cn.myafx.cache.GeoRadius;
import cn.myafx.cache.ICacheKey;
import cn.myafx.cache.RadiusOptions;
import cn.myafx.cache.Sort;

public class GeoCache extends RedisCache implements IGeoCache {
    
    /**
     * GeoCache
     * @param node 缓存key配置db节点
     * @param item 缓存key配置项
     * @param redisConnection RedisConnection
     * @param cacheKey ICacheKey
     * @param prefix 缓存前缀
     * @throws Exception
     */
    public GeoCache(String node, String item, RedisConnection redisConnection, ICacheKey cacheKey, String prefix) throws Exception {
        super(node, item, redisConnection, cacheKey, prefix);
    }

    /**
     * 添加位置或更新
     * @param name 位置名称
     * @param lon 经度
     * @param lat 纬度
     * @param args key 参数
     * @return
     * @throws Exception
     */
    @Override
    public boolean addOrUpdate(String name, double lon, double lat, Object... args) throws Exception{
        if (name == null || name.isEmpty()) throw new Exception("name is null!");
        var point = GeoPos.toPoint(lon, lat);
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var r = this.redis.geoAdd(getBytes(cachekey), point, getBytes(name));

        return r > 0;
    }
    /**
     * 添加位置或更新
     * @param name 位置名称
     * @param pos 位置
     * @param args key 参数
     * @return
     * @throws Exception
     */
    @Override
    public boolean addOrUpdate(String name, GeoPos pos, Object... args)throws Exception{
        if (pos == null) throw new Exception("pos is null!");
        return this.addOrUpdate(name, pos.Lon, pos.Lat, args);
    }
    /**
     * 添加位置或更新
     * @param m GeoInfo
     * @param args key 参数
     * @return
     * @throws Exception
     */
    @Override
    public boolean addOrUpdate(GeoInfo m, Object... args)throws Exception{
        if (m == null) throw new Exception("m is null!");
        return this.addOrUpdate(m.Name, m.Position, args);
    }
    /**
     * 添加位置或更新
     * @param list List GeoInfo
     * @param args key 参数
     * @return
     * @throws Exception
     */
    @Override
    public long addOrUpdate(List<GeoInfo> list, Object... args) throws Exception{
        if (list == null) throw new Exception("list is null!");
        if (list.size() == 0) return 0;
        Map<byte[], Point> map = new HashMap<>(list.size());
        for(var gm : list){
            if (gm == null) throw new Exception("list item is null!");
            if (gm.Name == null || gm.Name.isEmpty()) throw new Exception("list item.Name is null!");
            if (gm.Position == null) throw new Exception("list item.Position is null!");
            map.put(getBytes(gm.Name), new Point(gm.Position.Lon, gm.Position.Lat));
        }
        
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var r = this.redis.geoAdd(getBytes(cachekey), map);

        return r;
    }
    /**
     * 获取坐标
     * @param name 位置名称
     * @param args key 参数
     * @return GeoPos
     * @throws Exception
     */
    @Override
    public GeoPos get(String name, Object... args) throws Exception{
        if (name == null || name.isEmpty()) throw new Exception("name is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var rlist = this.redis.geoPos(getBytes(cachekey), getBytes(name));
        GeoPos m = null;
        if(rlist!=null && rlist.size() > 0){
            var p = rlist.get(0);
            if(p!= null) m = GeoPos.toGeoPos(p);
        }
        return m;
    }
    /**
     * 获取坐标
     * @param names 位置名称 List
     * @param args key 参数
     * @return List GeoPos
     * @throws Exception
     */
    @Override
    public List<GeoPos> get(List<String> names, Object... args) throws Exception{
        if (names == null) throw new Exception("names is null!");
        if(names.size() == 0) return new ArrayList<>(0);
        var marr = new byte[names.size()][];
        for(var i=0; i< names.size(); i++){
            var name = names.get(i);
            if (name == null || name.isEmpty()) throw new Exception("name is null!");
            marr[i] = getBytes(name);
        }
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var rlist = this.redis.geoPos(getBytes(cachekey), marr);
        var list = new ArrayList<GeoPos>(rlist.size());
        for(var p : rlist){
            list.add(GeoPos.toGeoPos(p));
        }

        return list;
    }

    private Metric getMetric(DistUnit unit){
        switch(unit){
            case km:
                return Metrics.KILOMETERS;
            case mi:
                return Metrics.MILES;
            case ft:
                return Metrics.FEET;
            case m:
            default:
                return Metrics.METERS;
        }
    }

    /**
     * 计算距离
     * @param firstName 第一个坐标点名称
     * @param secondName 第二个坐标点名称
     * @param unit 距离单位
     * @param args key 参数
     * @return 坐标不存在返回null
     * @throws Exception
     */
    @Override
    public Double getDist(String firstName, String secondName, DistUnit unit, Object... args)throws Exception{
        if (firstName == null || firstName.isEmpty()) throw new Exception("firstName is null!");
        if (secondName == null || secondName.isEmpty()) throw new Exception("secondName is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var r = this.redis.geoDist(getBytes(cachekey), getBytes(firstName), getBytes(secondName), this.getMetric(unit));

        return r != null ? r.getValue() : null;
    }
    /**
     * 获取GeoHash
     * @param name 位置名称
     * @param args key 参数
     * @return
     * @throws Exception
     */
    @Override
    public String getGeoHash(String name, Object... args)throws Exception{
        if (name == null || name.isEmpty()) throw new Exception("name is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var rlist = this.redis.geoHash(getBytes(cachekey), getBytes(name));

        return rlist != null && rlist.size() > 0 ? rlist.get(0) : null;
    }
    /**
     * 获取GeoHash
     * @param names 位置名称 List
     * @param args key 参数
     * @return
     * @throws Exception
     */
    @Override
    public List<String> getGeoHash(List<String> names, Object... args)throws Exception{
        if (names == null) throw new Exception("names is null!");
        if(names.size() == 0) return new ArrayList<>(0);
        var marr = new byte[names.size()][];
        for(var i=0; i< names.size(); i++){
            var name = names.get(i);
            if (name == null || name.isEmpty()) throw new Exception("name is null!");
            marr[i] = getBytes(name);
        }
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var rlist = this.redis.geoHash(getBytes(cachekey), marr);

        return rlist;
    }
    /**
     * 删除位置点
     * @param name 位置名称
     * @param args key 参数
     * @return
     * @throws Exception
     */
    @Override
    public boolean delete(String name, Object... args)throws Exception{
        if (name == null || name.isEmpty()) throw new Exception("name is null!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var r = this.redis.geoRemove(getBytes(cachekey), getBytes(name));

        return r > 0;
    }

    private GeoRadiusCommandArgs getOption(int radiusOptions,int count, Sort sort){
        var op = GeoRadiusCommandArgs.newGeoRadiusArgs();
        if(radiusOptions == RadiusOptions.None || (radiusOptions & RadiusOptions.WithCoordinates) == RadiusOptions.WithCoordinates) 
            op.includeCoordinates();
        if(radiusOptions == RadiusOptions.None || (radiusOptions & RadiusOptions.WithDistance) == RadiusOptions.WithDistance) 
            op.includeDistance();
        if(count > 0) op.limit(count);
        op.sort(sort == Sort.Asc ? Direction.ASC : Direction.DESC);

        return op;
    }

    /**
     * 查询指定位置名称半径内的位置
     * @param name 位置名称
     * @param radius 半径
     * @param unit 半径单位
     * @param count 返回数量， -1 or 0返回所有
     * @param sort 排序，Asc 由近到远
     * @param radiusOptions 返回数据选项
     * @param args key 参数
     * @return
     * @throws Exception
     */
    @Override
    public List<GeoRadius> getRadius(String name, double radius, DistUnit unit, int count, Sort sort, int radiusOptions, Object... args)throws Exception{
        if (name == null || name.isEmpty()) throw new Exception("name is null!");
        if (radius < 0) throw new Exception("radius = "+radius+" is error!");
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var distance = new Distance(radius, getMetric(unit));
        var op = this.getOption(radiusOptions, count, sort);
        var rlist = this.redis.geoRadiusByMember(getBytes(cachekey), getBytes(name), distance, op);
        ArrayList<GeoRadius> list = null;
        if(rlist!= null){
            var rclist = rlist.getContent();
            list = new ArrayList<>(rclist.size());
            for(var gr : rclist){
                GeoRadius m = null;
                if(gr != null){
                    var gn = gr.getContent();
                    var d = gr.getDistance();
                    var p = gn.getPoint();
                    String rname = getString(gn.getName());
                    Double rdistance = d != null ? d.getValue() : null;
                    GeoPos rposition = GeoPos.toGeoPos(p);
                    m = new GeoRadius(rname, rdistance, rposition);
                }
                list.add(m);
            }
        }

        return list;
    }
    /**
     * 查询指定坐标半径内的位置
     * @param lon 经度
     * @param lat 纬度
     * @param radius 半径
     * @param unit 半径单位
     * @param count 返回数量， -1返回所有
     * @param sort 排序，Asc 由近到远
     * @param radiusOptions 返回数据选项
     * @param args key 参数
     * @return
     * @throws Exception
     */
    @Override
    public List<GeoRadius> getRadius(double lon, double lat, double radius, DistUnit unit, int count, Sort sort, int radiusOptions, Object... args)throws Exception{
        var point = GeoPos.toPoint(lon, lat);
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var distance = new Distance(radius, getMetric(unit));
        var circle = new Circle(point, distance);
        var op = this.getOption(radiusOptions, count, sort);
        var rlist = this.redis.geoRadius(getBytes(cachekey), circle, op);
        ArrayList<GeoRadius> list = null;
        if(rlist!= null){
            var rclist = rlist.getContent();
            list = new ArrayList<>(rclist.size());
            for(var gr : rclist){
                GeoRadius m = null;
                if(gr != null){
                    var gn = gr.getContent();
                    var d = gr.getDistance();
                    var p = gn.getPoint();
                    String rname = getString(gn.getName());
                    Double rdistance = d != null ? d.getValue() : null;
                    GeoPos rposition = GeoPos.toGeoPos(p);
                    m = new GeoRadius(rname, rdistance, rposition);
                }
                list.add(m);
            }
        }

        return list;
    }
    /**
     * 查询geo集合数量
     * @param args
     * @return
     * @throws Exception
     */
    @Override
    public long getCount(Object... args)throws Exception{
        String cachekey = this.getCacheKey(args);
        int db = this.getCacheDb(cachekey);
        this.redis.select(db);
        var count = this.redis.zCount(getBytes(cachekey), Double.MIN_VALUE, Double.MAX_VALUE);

        return count == null ? 0 : count;
    }
}
