package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;
import cn.cyc.ai.cog.runtime.api.CapabilityExecuteRequest;
import cn.cyc.ai.cog.runtime.spi.ExecutionParameterValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 默认执行参数校验器，按 Capability 与 Agent 元数据收口执行参数。
 *
 * @author cyc
 */
@Service
public class DefaultExecutionParameterValidator implements ExecutionParameterValidator {

    /**
     * 校验日志。
     */
    private static final Logger log = LoggerFactory.getLogger(DefaultExecutionParameterValidator.class);

    /**
     * 校验执行参数白名单与数值范围。
     *
     * @param request    能力执行请求
     * @param capability 能力定义
     * @param agent      Agent 定义
     */
    @Override
    public void validate(CapabilityExecuteRequest request, CapabilityDefinition capability, AgentDefinition agent) {
        Map<String, Object> parameters = request.parameters();
        Map<String, ParameterConstraintDefinition> constraints = mergeConstraints(capability.parameterConstraints(), agent.parameterConstraints());
        if (parameters.isEmpty() && constraints.isEmpty()) {
            return;
        }
        log.info("开始校验执行参数, capabilityCode={}, parameterKeys={}, constraintKeys={}",
                request.capabilityCode(), parameters.keySet(), constraints.keySet());
        parameters.forEach((parameterName, value) -> validateParameter(parameterName, value, constraints));
        constraints.forEach((parameterName, constraint) -> validateRequiredParameter(parameterName, constraint, parameters));
    }

    /**
     * 合并 Capability 与 Agent 两层参数约束。
     *
     * @param capabilityConstraints Capability 约束
     * @param agentConstraints      Agent 约束
     * @return 生效约束
     */
    private Map<String, ParameterConstraintDefinition> mergeConstraints(
            Map<String, ParameterConstraintDefinition> capabilityConstraints,
            Map<String, ParameterConstraintDefinition> agentConstraints) {
        if (agentConstraints.isEmpty()) {
            return capabilityConstraints;
        }
        Map<String, ParameterConstraintDefinition> merged = new java.util.LinkedHashMap<>(capabilityConstraints);
        agentConstraints.forEach((parameterName, agentConstraint) -> {
            ParameterConstraintDefinition capabilityConstraint = capabilityConstraints.get(parameterName);
            if (capabilityConstraint == null) {
                throw new BusinessException("CONFLICT", "Agent 参数约束超出 Capability 范围: " + parameterName);
            }
            merged.put(parameterName, tightenConstraint(parameterName, capabilityConstraint, agentConstraint));
        });
        return Map.copyOf(merged);
    }

    /**
     * 基于 Capability 约束收紧 Agent 约束。
     *
     * @param parameterName        参数名
     * @param capabilityConstraint Capability 约束
     * @param agentConstraint      Agent 约束
     * @return 收紧后的约束
     */
    private ParameterConstraintDefinition tightenConstraint(String parameterName,
                                                            ParameterConstraintDefinition capabilityConstraint,
                                                            ParameterConstraintDefinition agentConstraint) {
        if (!capabilityConstraint.parameterType().equals(agentConstraint.parameterType())) {
            throw new BusinessException("CONFLICT", "Agent 参数类型与 Capability 不一致: " + parameterName);
        }
        if (capabilityConstraint.integerOnly() != agentConstraint.integerOnly()
                && "integer".equals(capabilityConstraint.parameterType())) {
            throw new BusinessException("CONFLICT", "Agent 整数约束与 Capability 不一致: " + parameterName);
        }
        Double effectiveMinimum = chooseMinimum(capabilityConstraint.minimum(), agentConstraint.minimum());
        Double effectiveMaximum = chooseMaximum(capabilityConstraint.maximum(), agentConstraint.maximum());
        if (effectiveMinimum != null && effectiveMaximum != null && effectiveMinimum > effectiveMaximum) {
            throw new BusinessException("CONFLICT", "Agent 参数约束与 Capability 冲突: " + parameterName);
        }
        return new ParameterConstraintDefinition(
                capabilityConstraint.parameterType(),
                capabilityConstraint.required() || agentConstraint.required(),
                effectiveMinimum,
                effectiveMaximum,
                capabilityConstraint.integerOnly() || agentConstraint.integerOnly()
        );
    }

    /**
     * 选择更严格的最小值。
     *
     * @param left  第一层最小值
     * @param right 第二层最小值
     * @return 生效最小值
     */
    private Double chooseMinimum(Double left, Double right) {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        return Math.max(left, right);
    }

    /**
     * 选择更严格的最大值。
     *
     * @param left  第一层最大值
     * @param right 第二层最大值
     * @return 生效最大值
     */
    private Double chooseMaximum(Double left, Double right) {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        return Math.min(left, right);
    }

    /**
     * 校验单个执行参数。
     *
     * @param parameterName 参数名
     * @param value         参数值
     * @param constraints   参数约束映射
     */
    private void validateParameter(String parameterName, Object value, Map<String, ParameterConstraintDefinition> constraints) {
        ParameterConstraintDefinition constraint = constraints.get(parameterName);
        if (constraint == null) {
            throw new BusinessException("INVALID_ARGUMENT", "不支持的执行参数: " + parameterName);
        }
        switch (constraint.parameterType()) {
            case "number" -> validateNumberParameter(parameterName, value, constraint);
            case "integer" -> validateIntegerParameter(parameterName, value, constraint);
            default -> throw new BusinessException("INVALID_ARGUMENT", "不支持的参数类型: " + constraint.parameterType());
        }
    }

    /**
     * 校验 number 类型参数范围。
     *
     * @param parameterName 参数名
     * @param value         参数值
     * @param constraint    参数约束
     */
    private void validateNumberParameter(String parameterName, Object value, ParameterConstraintDefinition constraint) {
        double number = requireNumber(parameterName, value);
        validateRange(parameterName, number, constraint);
        if (constraint.integerOnly() && number % 1 != 0) {
            throw new BusinessException("INVALID_ARGUMENT", parameterName + " 必须是整数");
        }
    }

    /**
     * 校验 integer 类型参数范围。
     *
     * @param parameterName 参数名
     * @param value         参数值
     * @param constraint    参数约束
     */
    private void validateIntegerParameter(String parameterName, Object value, ParameterConstraintDefinition constraint) {
        if (!(value instanceof Number numberValue)) {
            throw new BusinessException("INVALID_ARGUMENT", parameterName + " 必须是整数");
        }
        double rawNumber = numberValue.doubleValue();
        if (rawNumber % 1 != 0) {
            throw new BusinessException("INVALID_ARGUMENT", parameterName + " 必须是整数");
        }
        validateRange(parameterName, numberValue.intValue(), constraint);
    }

    /**
     * 读取并校验数值型参数。
     *
     * @param parameterName 参数名
     * @param value         参数值
     * @return 数值结果
     */
    private double requireNumber(String parameterName, Object value) {
        if (!(value instanceof Number numberValue)) {
            throw new BusinessException("INVALID_ARGUMENT", parameterName + " 必须是数值");
        }
        return numberValue.doubleValue();
    }

    /**
     * 校验必填参数是否存在。
     *
     * @param parameterName 参数名
     * @param constraint    参数约束
     * @param parameters    参数映射
     */
    private void validateRequiredParameter(String parameterName,
                                           ParameterConstraintDefinition constraint,
                                           Map<String, Object> parameters) {
        if (constraint.required() && !parameters.containsKey(parameterName)) {
            throw new BusinessException("INVALID_ARGUMENT", "缺少执行参数: " + parameterName);
        }
    }

    /**
     * 校验参数取值范围。
     *
     * @param parameterName 参数名
     * @param value         参数值
     * @param constraint    参数约束
     */
    private void validateRange(String parameterName, double value, ParameterConstraintDefinition constraint) {
        if (constraint.minimum() != null && value < constraint.minimum()) {
            throw buildRangeException(parameterName, constraint);
        }
        if (constraint.maximum() != null && value > constraint.maximum()) {
            throw buildRangeException(parameterName, constraint);
        }
    }

    /**
     * 构造范围校验异常。
     *
     * @param parameterName 参数名
     * @param constraint    参数约束
     * @return 业务异常
     */
    private BusinessException buildRangeException(String parameterName, ParameterConstraintDefinition constraint) {
        String message;
        if (constraint.minimum() != null && constraint.maximum() != null) {
            message = parameterName + " 取值范围必须在 " + formatNumber(constraint.minimum())
                    + " 到 " + formatNumber(constraint.maximum()) + " 之间";
        } else if (constraint.minimum() != null) {
            message = parameterName + " 取值必须大于等于 " + formatNumber(constraint.minimum());
        } else {
            message = parameterName + " 取值必须小于等于 " + formatNumber(constraint.maximum());
        }
        return new BusinessException("INVALID_ARGUMENT", message);
    }

    /**
     * 格式化数字文本，避免 1.0 这类展示噪声。
     *
     * @param value 数值
     * @return 格式化结果
     */
    private String formatNumber(Double value) {
        if (value == null) {
            return "";
        }
        if (value % 1 == 0) {
            return String.valueOf(value.intValue());
        }
        return value.toString();
    }
}
