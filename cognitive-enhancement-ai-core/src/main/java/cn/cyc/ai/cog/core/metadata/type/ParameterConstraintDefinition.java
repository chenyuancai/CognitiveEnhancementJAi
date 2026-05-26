package cn.cyc.ai.cog.core.metadata.type;

import java.util.Objects;

/**
 * 执行参数约束定义，用于描述单个参数的类型与取值边界。
 *
 * @param parameterType 参数类型
 * @param required      是否必填
 * @param minimum       最小值
 * @param maximum       最大值
 * @param integerOnly   是否仅允许整数
 * @author cyc
 */
public record ParameterConstraintDefinition(
        String parameterType,
        boolean required,
        Double minimum,
        Double maximum,
        boolean integerOnly
) {

    /**
     * 构造参数约束定义并完成基础校验。
     */
    public ParameterConstraintDefinition {
        parameterType = Objects.requireNonNull(parameterType, "parameterType 不能为空");
        if (minimum != null && maximum != null && minimum > maximum) {
            throw new IllegalArgumentException("minimum 不能大于 maximum");
        }
    }
}
