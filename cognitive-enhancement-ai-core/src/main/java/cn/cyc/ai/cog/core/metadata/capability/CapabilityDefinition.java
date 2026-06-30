package cn.cyc.ai.cog.core.metadata.capability;

import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * 能力定义对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record CapabilityDefinition(
        String capabilityCode,
        String capabilityName,
        String capabilityDesc,
        SchemaDefinition inputSchema,
        SchemaDefinition outputSchema,
        Map<String, ParameterConstraintDefinition> parameterConstraints,
        ExecutionMode executeMode,
        String boundAgentCode,
        RiskLevel riskLevel,
        boolean needHumanConfirm,
        CommonStatus status,
        String version,
        Instant publishedAt,
        CapabilityLifecycleStatus lifecycleStatus
) implements MetadataDefinition {

    public CapabilityDefinition {
        capabilityCode = Objects.requireNonNull(capabilityCode, "capabilityCode 不能为空");
        capabilityName = Objects.requireNonNull(capabilityName, "capabilityName 不能为空");
        capabilityDesc = Objects.requireNonNull(capabilityDesc, "capabilityDesc 不能为空");
        inputSchema = Objects.requireNonNull(inputSchema, "inputSchema 不能为空");
        outputSchema = Objects.requireNonNull(outputSchema, "outputSchema 不能为空");
        parameterConstraints = Map.copyOf(parameterConstraints == null ? Map.of() : parameterConstraints);
        executeMode = Objects.requireNonNull(executeMode, "executeMode 不能为空");
        boundAgentCode = Objects.requireNonNull(boundAgentCode, "boundAgentCode 不能为空");
        riskLevel = Objects.requireNonNull(riskLevel, "riskLevel 不能为空");
        status = Objects.requireNonNull(status, "status 不能为空");
        if (version == null || version.isBlank()) {
            version = "1.0.0";
        }
        if (lifecycleStatus == null) {
            lifecycleStatus = CapabilityLifecycleStatus.PUBLISHED;
        }
    }

    /**
     * 兼容旧构造：默认版本 1.0.0、已发布。
     */
    public CapabilityDefinition(String capabilityCode,
                                  String capabilityName,
                                  String capabilityDesc,
                                  SchemaDefinition inputSchema,
                                  SchemaDefinition outputSchema,
                                  Map<String, ParameterConstraintDefinition> parameterConstraints,
                                  ExecutionMode executeMode,
                                  String boundAgentCode,
                                  RiskLevel riskLevel,
                                  boolean needHumanConfirm,
                                  CommonStatus status) {
        this(capabilityCode, capabilityName, capabilityDesc, inputSchema, outputSchema, parameterConstraints,
                executeMode, boundAgentCode, riskLevel, needHumanConfirm, status,
                "1.0.0", Instant.now(), CapabilityLifecycleStatus.PUBLISHED);
    }

    /**
     * 执行编码。
     * @return 执行结果
     */
    @Override
    public String code() {
        return capabilityCode;
    }

    /**
     * 执行名称。
     * @return 执行结果
     */
    @Override
    public String name() {
        return capabilityName;
    }

    /**
     * 是否可被运行时直接解析（已发布且启用）。
     *
     * @return 是否运行时可见
     */
    public boolean runtimeVisible() {
        return status == CommonStatus.ENABLED && lifecycleStatus == CapabilityLifecycleStatus.PUBLISHED;
    }
}
