package cn.cyc.ai.cog.center.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * JSON 序列化/反序列化工具，用于 Entity 与 Record 之间的复杂字段转换。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class JsonConverter {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(JsonConverter.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * 创建JsonConverter。
     */
    private JsonConverter() {
    }

    /**
     * 将对象序列化为 JSON 字符串。
     */
    public static String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            log.error("JSON 序列化失败", e);
            throw new IllegalStateException("JSON 序列化失败", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为指定类型。
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.error("JSON 反序列化失败, json={}", json, e);
            throw new IllegalStateException("JSON 反序列化失败", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为字符串列表。
     */
    public static List<String> stringListFromJson(String json) {
        if (json == null || json.isEmpty()) {
            return List.of();
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            log.error("JSON 反序列化失败, json={}", json, e);
            throw new IllegalStateException("JSON 反序列化失败", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为指定类型的 Map。
     */
    public static <V> Map<String, V> mapFromJson(String json, Class<V> valueClass) {
        if (json == null || json.isEmpty()) {
            return Map.of();
        }
        try {
            return OBJECT_MAPPER.readValue(json, OBJECT_MAPPER.getTypeFactory()
                    .constructMapType(Map.class, String.class, valueClass));
        } catch (Exception e) {
            log.error("JSON 反序列化失败, json={}", json, e);
            throw new IllegalStateException("JSON 反序列化失败", e);
        }
    }
}
