package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.runtime.api.CapabilityExecuteRequest;
import cn.cyc.ai.cog.runtime.spi.InputSchemaValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 默认能力输入 Schema 校验器。
 *
 * 当前版本优先校验已提供字段的类型与枚举约束，
 * 不在这一层重复处理 Prompt 必填变量缺失问题。
 *
 * @author cyc
 */
@Component
public class DefaultInputSchemaValidator implements InputSchemaValidator {

    /**
     * 校验日志。
     */
    private static final Logger log = LoggerFactory.getLogger(DefaultInputSchemaValidator.class);

    /**
     * 校验能力请求输入。
     *
     * @param request    能力执行请求
     * @param capability 能力定义
     */
    @Override
    public void validate(CapabilityExecuteRequest request, CapabilityDefinition capability) {
        SchemaDefinition inputSchema = capability.inputSchema();
        if (inputSchema == null) {
            return;
        }
        log.info("开始校验能力输入 Schema, capabilityCode={}, inputKeys={}",
                capability.capabilityCode(),
                request.input().keySet());
        validateValue("input", request.input(), inputSchema);
    }

    /**
     * 递归校验单个值。
     *
     * @param path   当前字段路径
     * @param value  当前字段值
     * @param schema 当前字段 Schema
     */
    private void validateValue(String path, Object value, SchemaDefinition schema) {
        if (value == null || schema == null) {
            return;
        }
        switch (schema.type()) {
            case "object" -> validateObject(path, value, schema);
            case "array" -> validateArray(path, value, schema);
            case "string" -> validateString(path, value, schema);
            case "integer" -> validateInteger(path, value);
            case "number" -> validateNumber(path, value);
            case "boolean" -> validateBoolean(path, value);
            default -> log.warn("遇到未支持的 Schema 类型，暂跳过校验, path={}, type={}", path, schema.type());
        }
    }

    /**
     * 校验对象结构。
     *
     * @param path   当前字段路径
     * @param value  字段值
     * @param schema 对象 Schema
     */
    private void validateObject(String path, Object value, SchemaDefinition schema) {
        if (!(value instanceof Map<?, ?> valueMap)) {
            throw invalidType(path, "object");
        }
        for (Map.Entry<String, SchemaDefinition> entry : schema.properties().entrySet()) {
            String propertyName = entry.getKey();
            if (!valueMap.containsKey(propertyName)) {
                continue;
            }
            Object propertyValue = valueMap.get(propertyName);
            validateValue(resolveChildPath(path, propertyName), propertyValue, entry.getValue());
        }
    }

    /**
     * 校验数组结构。
     *
     * @param path   当前字段路径
     * @param value  字段值
     * @param schema 数组 Schema
     */
    private void validateArray(String path, Object value, SchemaDefinition schema) {
        if (!(value instanceof List<?> values)) {
            throw invalidType(path, "array");
        }
        if (schema.items() == null) {
            return;
        }
        for (int index = 0; index < values.size(); index++) {
            validateValue(path + "[" + index + "]", values.get(index), schema.items());
        }
    }

    /**
     * 校验字符串值与枚举约束。
     *
     * @param path   当前字段路径
     * @param value  字段值
     * @param schema 字符串 Schema
     */
    private void validateString(String path, Object value, SchemaDefinition schema) {
        if (!(value instanceof String stringValue)) {
            throw invalidType(path, "string");
        }
        if (!schema.enumValues().isEmpty() && !schema.enumValues().contains(stringValue)) {
            throw new BusinessException("INVALID_ARGUMENT",
                    "输入参数 " + normalizePath(path) + " 必须是枚举值: " + String.join(", ", schema.enumValues()));
        }
    }

    /**
     * 校验整数值。
     *
     * @param path  当前字段路径
     * @param value 字段值
     */
    private void validateInteger(String path, Object value) {
        if (!(value instanceof Number numberValue) || !isIntegerNumber(numberValue)) {
            throw invalidType(path, "integer");
        }
    }

    /**
     * 校验数值类型。
     *
     * @param path  当前字段路径
     * @param value 字段值
     */
    private void validateNumber(String path, Object value) {
        if (!(value instanceof Number)) {
            throw invalidType(path, "number");
        }
    }

    /**
     * 校验布尔类型。
     *
     * @param path  当前字段路径
     * @param value 字段值
     */
    private void validateBoolean(String path, Object value) {
        if (!(value instanceof Boolean)) {
            throw invalidType(path, "boolean");
        }
    }

    /**
     * 判断数值是否为整数。
     *
     * @param value 数值对象
     * @return 是否整数
     */
    private boolean isIntegerNumber(Number value) {
        if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
            return true;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.stripTrailingZeros().scale() <= 0;
        }
        double doubleValue = value.doubleValue();
        return !Double.isNaN(doubleValue) && !Double.isInfinite(doubleValue) && Math.rint(doubleValue) == doubleValue;
    }

    /**
     * 生成子字段路径。
     *
     * @param parentPath   父路径
     * @param propertyName 子字段名称
     * @return 子路径
     */
    private String resolveChildPath(String parentPath, String propertyName) {
        if ("input".equals(parentPath)) {
            return propertyName;
        }
        return parentPath + "." + propertyName;
    }

    /**
     * 构造类型不匹配异常。
     *
     * @param path         当前字段路径
     * @param expectedType 期望类型
     * @return 业务异常
     */
    private BusinessException invalidType(String path, String expectedType) {
        return new BusinessException("INVALID_ARGUMENT",
                "输入参数 " + normalizePath(path) + " 必须是 " + expectedType);
    }

    /**
     * 归一化路径展示。
     *
     * @param path 原始路径
     * @return 展示路径
     */
    private String normalizePath(String path) {
        return "input".equals(path) ? "input" : path;
    }
}
