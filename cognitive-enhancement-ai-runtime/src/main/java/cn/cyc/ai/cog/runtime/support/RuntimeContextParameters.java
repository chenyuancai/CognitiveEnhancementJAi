package cn.cyc.ai.cog.runtime.support;

import cn.cyc.ai.cog.core.runtime.ExecutionContext;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Runtime 治理参数读取工具。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class RuntimeContextParameters {

    /**
     * 创建RuntimeContextParameters。
     */
    private RuntimeContextParameters() {
    }

    /**
     * 读取布尔开关参数。
     *
     * @param context 运行时上下文
     * @param key     参数名
     * @return 是否启用
     */
    public static boolean flag(ExecutionContext context, String key) {
        Object value = context.request().parameters().get(key);
        if (value instanceof Boolean enabled) {
            return enabled;
        }
        if (value instanceof String text) {
            return Boolean.parseBoolean(text);
        }
        return false;
    }

    /**
     * 读取字符串列表参数，支持 List 或逗号分隔字符串。
     *
     * @param context 运行时上下文
     * @param key     参数名
     * @return 字符串列表
     */
    public static List<String> stringList(ExecutionContext context, String key) {
        Object value = context.request().parameters().get(key);
        if (value instanceof List<?> list) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .filter(item -> !item.isBlank())
                    .toList();
        }
        if (value instanceof String text && !text.isBlank()) {
            return Arrays.stream(text.split(","))
                    .map(String::trim)
                    .filter(item -> !item.isBlank())
                    .toList();
        }
        return List.of();
    }

    /**
     * 读取 BigDecimal 参数。
     *
     * @param context 运行时上下文
     * @param key     参数名
     * @return 数值
     */
    public static Optional<BigDecimal> decimal(ExecutionContext context, String key) {
        Object value = context.request().parameters().get(key);
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof BigDecimal decimal) {
            return Optional.of(decimal);
        }
        if (value instanceof Number number) {
            return Optional.of(BigDecimal.valueOf(number.doubleValue()));
        }
        if (value instanceof String text && !text.isBlank()) {
            return Optional.of(new BigDecimal(text.trim()));
        }
        return Optional.empty();
    }

    /**
     * 读取整数参数。
     *
     * @param context 运行时上下文
     * @param key     参数名
     * @return 整数值
     */
    public static Optional<Integer> integer(ExecutionContext context, String key) {
        Object value = context.request().parameters().get(key);
        if (value == null) {
            return Optional.empty();
        }
        if (value instanceof Number number) {
            return Optional.of(number.intValue());
        }
        if (value instanceof String text && !text.isBlank()) {
            return Optional.of(Integer.parseInt(text.trim()));
        }
        return Optional.empty();
    }

    /**
     * 读取字符串参数。
     *
     * @param context 运行时上下文
     * @param key     参数名
     * @return 字符串值
     */
    public static Optional<String> stringValue(ExecutionContext context, String key) {
        Object value = context.request().parameters().get(key);
        if (value == null) {
            return Optional.empty();
        }
        String text = String.valueOf(value);
        return text.isBlank() ? Optional.empty() : Optional.of(text.trim());
    }
}
