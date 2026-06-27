package cn.cyc.ai.cog.center.tool;

import cn.cyc.ai.cog.center.common.SchemaDto;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;

/**
 * Tool DTO 定义。
 */
public final class ToolDtos {

    private ToolDtos() {
    }

    public record CreateRequest(
            String toolCode,
            String toolName,
            ToolProtocolType protocolType,
            SchemaDto requestSchema,
            SchemaDto responseSchema,
            String permissionScope,
            RiskLevel riskLevel,
            int timeoutMs,
            int retryMaxAttempts,
            String implRef,
            CommonStatus status
    ) {
    }

    public record UpdateRequest(
            String toolName,
            ToolProtocolType protocolType,
            SchemaDto requestSchema,
            SchemaDto responseSchema,
            String permissionScope,
            RiskLevel riskLevel,
            int timeoutMs,
            int retryMaxAttempts,
            String implRef,
            CommonStatus status
    ) {
    }

    public record Result(
            String toolCode,
            String toolName,
            ToolProtocolType protocolType,
            SchemaDto requestSchema,
            SchemaDto responseSchema,
            String permissionScope,
            RiskLevel riskLevel,
            int timeoutMs,
            int retryMaxAttempts,
            String implRef,
            CommonStatus status
    ) {
    }
}
