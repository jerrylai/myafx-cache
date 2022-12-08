package cn.myafx.cache;

public interface IJsonMapper {

    /**
     * to json
     * 
     * @param <T>
     * @param m
     * @return
     */
    <T> String serialize(T m) throws Exception;

    /**
     * 
     * @param <T>
     * @param json
     * @param clazz
     * @return
     */
    <T> T deserialize(String json, Class<T> clazz) throws Exception;
}
