package cn.cyc.ai.cog.core.metadata.tool;

import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

import java.util.Objects;

/**
 * Tool 定义对象。
 */
public record ToolDefinition(
        String toolCode,
        String toolName,
        ToolProtocolType protocolType,
        SchemaDefinition requestSchema,
        SchemaDefinition responseSchema,
        String permissionScope,
        int timeoutMs,
        RetryPolicy retryPolicy,
        String implRef,
        CommonStatus status
) implements MetadataDefinition {

    public ToolDefinition {
        toolCode = Objects.requireNonNull(toolCode, "toolCode 不能为空");
        toolName = Objects.requireNonNull(toolName, "toolName 不能为空");
        protocolType = Objects.requireNonNull(protocolType, "protocolType 不能为空");
        requestSchema = Objects.requireNonNull(requestSchema, "requestSchema 不能为空");
        responseSchema = Objects.requireNonNull(responseSchema, "responseSchema 不能为空");
        permissionScope = Objects.requireNonNull(permissionScope, "permissionScope 不能为空");
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("timeoutMs 必须大于 0");
        }
        retryPolicy = Objects.requireNonNull(retryPolicy, "retryPolicy 不能为空");
        implRef = Objects.requireNonNull(implRef, "implRef 不能为空");
        status = Objects.requireNonNull(status, "status 不能为空");
    }

    @Override
    public String code() {
        return toolCode;
    }

    @Override
    public String name() {
        return toolName;
    }
}
