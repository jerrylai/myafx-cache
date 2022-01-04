package cn.myafx.cache;

import cn.myafx.cache.db.IGeoDbCache;

public class App {
    @SuppressWarnings("unchecked")
   public static void main( String[] args )throws Exception{
        try (IGeoDbCache cache = RedisUtils.getCache("VehGps", IGeoDbCache.class)) {
            var s = cache.getCount();
            
            System.out.println(s);
        }
    }
}
