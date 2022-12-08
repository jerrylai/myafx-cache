package cn.myafx.cache.base;

import java.util.List;

import cn.myafx.cache.DistUnit;
import cn.myafx.cache.GeoInfo;
import cn.myafx.cache.GeoPos;
import cn.myafx.cache.GeoRadius;
import cn.myafx.cache.Sort;

public interface IGeoCache extends IRedisCache {
        /**
         * 添加位置或更新
         * 
         * @param name 位置名称
         * @param lon  经度
         * @param lat  纬度
         * @param args key 参数
         * @return
         * @throws Exception
         */
        boolean addOrUpdate(String name, double lon, double lat, Object... args) throws Exception;

        /**
         * 添加位置或更新
         * 
         * @param name 位置名称
         * @param pos  位置
         * @param args key 参数
         * @return
         * @throws Exception
         */
        boolean addOrUpdate(String name, GeoPos pos, Object... args) throws Exception;

        /**
         * 添加位置或更新
         * 
         * @param m    GeoInfo
         * @param args key 参数
         * @return
         * @throws Exception
         */
        boolean addOrUpdate(GeoInfo m, Object... args) throws Exception;

        /**
         * 添加位置或更新
         * 
         * @param list List GeoInfo
         * @param args key 参数
         * @return
         * @throws Exception
         */
        long addOrUpdate(List<GeoInfo> list, Object... args) throws Exception;

        /**
         * 获取坐标
         * 
         * @param name 位置名称
         * @param args key 参数
         * @return GeoPos
         * @throws Exception
         */
        GeoPos get(String name, Object... args) throws Exception;

        /**
         * 获取坐标
         * 
         * @param names 位置名称 List
         * @param args  key 参数
         * @return List GeoPos
         * @throws Exception
         */
        List<GeoPos> get(List<String> names, Object... args) throws Exception;

        /**
         * 计算距离
         * 
         * @param firstName  第一个坐标点名称
         * @param secondName 第二个坐标点名称
         * @param unit       距离单位
         * @param args       key 参数
         * @return 坐标不存在返回null
         * @throws Exception
         */
        Double getDist(String firstName, String secondName, DistUnit unit, Object... args) throws Exception;

        /**
         * 获取GeoHash
         * 
         * @param name 位置名称
         * @param args key 参数
         * @return
         * @throws Exception
         */
        String getGeoHash(String name, Object... args) throws Exception;

        /**
         * 获取GeoHash
         * 
         * @param names 位置名称 List
         * @param args  key 参数
         * @return
         * @throws Exception
         */
        List<String> getGeoHash(List<String> names, Object... args) throws Exception;

        /**
         * 删除位置点
         * 
         * @param name 位置名称
         * @param args key 参数
         * @return
         * @throws Exception
         */
        boolean delete(String name, Object... args) throws Exception;

        /**
         * 查询指定位置名称半径内的位置
         * 
         * @param name          位置名称
         * @param radius        半径
         * @param unit          半径单位
         * @param count         返回数量， -1返回所有
         * @param sort          排序，Asc 由近到远
         * @param radiusOptions 返回数据选项
         * @param args          key 参数
         * @return
         * @throws Exception
         */
        List<GeoRadius> getRadius(String name, double radius, DistUnit unit, int count, Sort sort, int radiusOptions,
                        Object... args) throws Exception;

        /**
         * 查询指定坐标半径内的位置
         * 
         * @param lon           经度
         * @param lat           纬度
         * @param radius        半径
         * @param unit          半径单位
         * @param count         返回数量， -1返回所有
         * @param sort          排序，Asc 由近到远
         * @param radiusOptions 返回数据选项
         * @param args          key 参数
         * @return
         * @throws Exception
         */
        List<GeoRadius> getRadius(double lon, double lat, double radius, DistUnit unit, int count, Sort sort,
                        int radiusOptions, Object... args) throws Exception;

        /**
         * 查询geo集合数量
         * 
         * @param args key 参数
         * @return
         * @throws Exception
         */
        long getCount(Object... args) throws Exception;
}
