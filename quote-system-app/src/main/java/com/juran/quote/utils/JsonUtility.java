package com.juran.quote.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.util.Map;

public class JsonUtility {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * @param object 对象实例.
     * @return json字符串
     */
    public static String objectToJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * @param jsonString json字符串.
     * @param classType  目标对象类型
     * @param <T>        泛型
     * @return 目标对象实例
     */
    public static <T> T jsonToObject(String jsonString, Class<T> classType) {
        try {
            return mapper.readValue(jsonString, classType);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * json 转 map
     *
     * @param jsonString json字符串.
     * @return map对象
     */
    public static Map jsonToMap(String jsonString) throws IOException {
        return mapper.readValue(jsonString, Map.class);
    }

    /**
     * map 转 json
     *
     * @param map Map对象.
     * @return json字符串
     */
    public static String mapToJson(Map map) throws IOException {
        return mapper.writeValueAsString(map);
    }

    public static String getChannel(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
