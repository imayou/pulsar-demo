package com.ayou.pulsardemo.infrastructure.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JSONUtil
 *
 * @author ysy
 * @blame ysy
 * @date 2020-04-13
 */
public class JsonUtil {
    public static String toString(Object o) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String s = objectMapper.writeValueAsString(o);
            return s;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T toBean(String jsonData, Class<T> beanType) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        try {
            T t = objectMapper.readValue(jsonData, beanType);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> toArray(String jsonData, Class<T> bean) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, bean);
            List<T> lendReco = objectMapper.readValue(jsonData, javaType);
            return lendReco;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断字符串是否是合法的json格式
     *
     * @param jsonString
     * @return
     */
    public static boolean isJsonValid(String jsonString) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonString);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
