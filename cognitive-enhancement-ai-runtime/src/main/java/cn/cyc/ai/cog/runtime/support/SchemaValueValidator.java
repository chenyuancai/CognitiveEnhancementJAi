package cn.cyc.ai.cog.runtime.support;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Schema 值递归校验器，供能力输入/输出与 Tool 入参复用。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class SchemaValueValidator {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(SchemaValueValidator.class);

    /** root路径。 */
    private final String rootPath;
    /** parameterLabel。 */
    private final String parameterLabel;
    /** enforceRequiredFields。 */
    private final boolean enforceRequiredFields;

    /**
     * @param rootPath              根路径标识（如 input、output）
     * @param parameterLabel        错误消息中的参数称谓（如「输入参数」「Tool 输入参数」）
     * @param enforceRequiredFields 是否校验必填字段（输出 Schema 为 true，输入/Tool 为 false）
     */
    public SchemaValueValidator(String rootPath, String parameterLabel, boolean enforceRequiredFields) {
        this.rootPath = rootPath;
        this.parameterLabel = parameterLabel;
        this.enforceRequiredFields = enforceRequiredFields;
    }

    /**
     * 校验必填字段是否存在。
     *
     * @param path     当前路径
     * @param valueMap 对象值
     * @param schema   对象 Schema
     */
    public void validateRequiredProperties(String path, Map<?, ?> valueMap, SchemaDefinition schema) {
        for (Map.Entry<String, SchemaDefinition> entry : schema.properties().entrySet()) {
            String propertyName = entry.getKey();
            SchemaDefinition propertySchema = entry.getValue();
            if (propertySchema.required() && !valueMap.containsKey(propertyName)) {
                throw new BusinessException("INVALID_ARGUMENT",
                        parameterLabel + " " + normalizePath(resolveChildPath(path, propertyName)) + " 不能为空");
            }
            Object propertyValue = valueMap.get(propertyName);
            if (propertyValue instanceof Map<?, ?> nestedMap && "object".equals(propertySchema.type())) {
                validateRequiredProperties(resolveChildPath(path, propertyName), nestedMap, propertySchema);
            }
        }
    }

    /**
     * 递归校验单个值。
     *
     * @param path   当前字段路径
     * @param value  当前字段值
     * @param schema 当前字段 Schema
     */
    public void validateValue(String path, Object value, SchemaDefinition schema) {
        if (schema == null) {
            return;
        }
        if (value == null) {
            if (enforceRequiredFields && schema.required()) {
                throw new BusinessException("INVALID_ARGUMENT",
                        parameterLabel + " " + normalizePath(path) + " 不能为空");
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
            default -> log.warn("遇到未支持的 Schema 类型，暂跳过校验, path={}, type={}", path, schema.type());
        }
    }

    /**
     * 校验参数。
     *
     * @param path 路径
     * @param value 值
     * @param schema Schema
     */
    private void validateObject(String path, Object value, SchemaDefinition schema) {
        if (!(value instanceof Map<?, ?> valueMap)) {
            throw invalidType(path, "object");
        }
        for (Map.Entry<String, SchemaDefinition> entry : schema.properties().entrySet()) {
            String propertyName = entry.getKey();
            SchemaDefinition propertySchema = entry.getValue();
            Object propertyValue = valueMap.get(propertyName);
            if (enforceRequiredFields) {
                if (!valueMap.containsKey(propertyName) && !propertySchema.required()) {
                    continue;
                }
                validateValue(resolveChildPath(path, propertyName), propertyValue, propertySchema);
            } else if (valueMap.containsKey(propertyName)) {
                validateValue(resolveChildPath(path, propertyName), propertyValue, propertySchema);
            }
        }
    }

    /**
     * 校验参数。
     *
     * @param path 路径
     * @param value 值
     * @param schema Schema
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
     * 校验参数。
     *
     * @param path 路径
     * @param value 值
     * @param schema Schema
     */
    private void validateString(String path, Object value, SchemaDefinition schema) {
        if (!(value instanceof String stringValue)) {
            throw invalidType(path, "string");
        }
        if (!schema.enumValues().isEmpty() && !schema.enumValues().contains(stringValue)) {
            throw new BusinessException("INVALID_ARGUMENT",
                    parameterLabel + " " + normalizePath(path) + " 必须是枚举值: "
                            + String.join(", ", schema.enumValues()));
        }
    }

    /**
     * 校验参数。
     *
     * @param path 路径
     * @param value 值
     */
    private void validateInteger(String path, Object value) {
        if (!(value instanceof Number numberValue) || !isIntegerNumber(numberValue)) {
            throw invalidType(path, "integer");
        }
    }

    /**
     * 校验参数。
     *
     * @param path 路径
     * @param value 值
     */
    private void validateNumber(String path, Object value) {
        if (!(value instanceof Number)) {
            throw invalidType(path, "number");
        }
    }

    /**
     * 校验参数。
     *
     * @param path 路径
     * @param value 值
     */
    private void validateBoolean(String path, Object value) {
        if (!(value instanceof Boolean)) {
            throw invalidType(path, "boolean");
        }
    }

    /**
     * 判断是否为IntegerNumber。
     *
     * @param value 值
     * @return 是否满足条件
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
     * 执行resolveChild路径。
     *
     * @param parentPath parent路径
     * @param propertyName property名称
     * @return 执行结果
     */
    private String resolveChildPath(String parentPath, String propertyName) {
        if (rootPath.equals(parentPath)) {
            return propertyName;
        }
        return parentPath + "." + propertyName;
    }

    /**
     * 执行invalid类型。
     *
     * @param path 路径
     * @param expectedType expected类型
     * @return 执行结果
     */
    private BusinessException invalidType(String path, String expectedType) {
        return new BusinessException("INVALID_ARGUMENT",
                parameterLabel + " " + normalizePath(path) + " 必须是 " + expectedType);
    }

    /**
     * 执行normalize路径。
     *
     * @param path 路径
     * @return 执行结果
     */
    private String normalizePath(String path) {
        return rootPath.equals(path) ? rootPath : path;
    }
}
