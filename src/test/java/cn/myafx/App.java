package cn.myafx;

import cn.myafx.cache.db.IGeoDbCache;
import cn.myafx.cache.db.IParamDbCache;
import cn.myafx.utils.RedisUtils;

public class App {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        try (IGeoDbCache cache = RedisUtils.getCache("VehGps", IGeoDbCache.class)) {
            var s = cache.getCount();
            System.out.println(s);
        }
        try (IParamDbCache<Integer> cache = RedisUtils.getCache("Test", IParamDbCache.class, Integer.class)) {
            var s = cache.get("1");
            System.out.println(s);
        }
    }
}
