package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.runtime.api.ExecutionResult;
import cn.cyc.ai.cog.runtime.spi.OutputSchemaValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 默认能力输出 Schema 校验器。
 *
 * 当前版本只校验业务输出载荷 `businessOutput`，
 * 不把 runtime 附加元数据视为能力契约的一部分。
 *
 * @author cyc
 */
@Component
public class DefaultOutputSchemaValidator implements OutputSchemaValidator {

    /**
     * 校验日志。
     */
    private static final Logger log = LoggerFactory.getLogger(DefaultOutputSchemaValidator.class);

    /**
     * 校验输出结构。
     *
     * @param capability     能力定义
     * @param executionResult 执行结果
     */
    @Override
    public void validate(CapabilityDefinition capability, ExecutionResult executionResult) {
        SchemaDefinition outputSchema = capability.outputSchema();
        if (outputSchema == null) {
            return;
        }
        Object businessOutput = executionResult.output().get("businessOutput");
        log.info("开始校验能力输出 Schema, capabilityCode={}, resultStatus={}",
                capability.capabilityCode(),
                executionResult.status());
        validateValue("output", businessOutput, outputSchema);
    }

    /**
     * 递归校验输出值。
     *
     * @param path   当前字段路径
     * @param value  当前字段值
     * @param schema 当前字段 Schema
     */
    private void validateValue(String path, Object value, SchemaDefinition schema) {
        if (schema == null) {
            return;
        }
        if (value == null) {
            if (schema.required()) {
                throw new BusinessException("INVALID_ARGUMENT", "输出参数 " + normalizePath(path) + " 不能为空");
            }
            return;
        }
        switch (schema.type()) {
            case "object" -> validateObject(path, value, schema);
            case "array" -> validateArray(path, value, schema);
            case "string" -> validateString(path, value, schema);
            case "integer" -> validateInteger(path, value);
            case "number" -> validateNumber(path, value);
            case "boolean" -> validateBoolean(path, value);
            default -> log.warn("遇到未支持的输出 Schema 类型，暂跳过校验, path={}, type={}", path, schema.type());
        }
    }

    /**
     * 校验对象结构。
     *
     * @param path   当前字段路径
     * @param value  当前字段值
     * @param schema 对象 Schema
     */
    private void validateObject(String path, Object value, SchemaDefinition schema) {
        if (!(value instanceof Map<?, ?> valueMap)) {
            throw invalidType(path, "object");
        }
        for (Map.Entry<String, SchemaDefinition> entry : schema.properties().entrySet()) {
            String propertyName = entry.getKey();
            SchemaDefinition propertySchema = entry.getValue();
            Object propertyValue = valueMap.get(propertyName);
            if (!valueMap.containsKey(propertyName) && !propertySchema.required()) {
                continue;
            }
            validateValue(resolveChildPath(path, propertyName), propertyValue, propertySchema);
        }
    }

    /**
     * 校验数组结构。
     *
     * @param path   当前字段路径
     * @param value  当前字段值
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
     * 校验字符串结构与枚举范围。
     *
     * @param path   当前字段路径
     * @param value  当前字段值
     * @param schema 字符串 Schema
     */
    private void validateString(String path, Object value, SchemaDefinition schema) {
        if (!(value instanceof String stringValue)) {
            throw invalidType(path, "string");
        }
        if (!schema.enumValues().isEmpty() && !schema.enumValues().contains(stringValue)) {
            throw new BusinessException("INVALID_ARGUMENT",
                    "输出参数 " + normalizePath(path) + " 必须是枚举值: " + String.join(", ", schema.enumValues()));
        }
    }

    /**
     * 校验整数结构。
     *
     * @param path  当前字段路径
     * @param value 当前字段值
     */
    private void validateInteger(String path, Object value) {
        if (!(value instanceof Number numberValue) || !isIntegerNumber(numberValue)) {
            throw invalidType(path, "integer");
        }
    }

    /**
     * 校验数值结构。
     *
     * @param path  当前字段路径
     * @param value 当前字段值
     */
    private void validateNumber(String path, Object value) {
        if (!(value instanceof Number)) {
            throw invalidType(path, "number");
        }
    }

    /**
     * 校验布尔结构。
     *
     * @param path  当前字段路径
     * @param value 当前字段值
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
     * @return 是否为整数
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
     * @param propertyName 子字段名
     * @return 子字段路径
     */
    private String resolveChildPath(String parentPath, String propertyName) {
        if ("output".equals(parentPath)) {
            return propertyName;
        }
        return parentPath + "." + propertyName;
    }

    /**
     * 构造类型不匹配异常。
     *
     * @param path         字段路径
     * @param expectedType 期望类型
     * @return 业务异常
     */
    private BusinessException invalidType(String path, String expectedType) {
        return new BusinessException("INVALID_ARGUMENT",
                "输出参数 " + normalizePath(path) + " 必须是 " + expectedType);
    }

    /**
     * 归一化路径展示。
     *
     * @param path 原始路径
     * @return 展示路径
     */
    private String normalizePath(String path) {
        return "output".equals(path) ? "output" : path;
    }
}
