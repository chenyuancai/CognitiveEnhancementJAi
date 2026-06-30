package cn.cyc.ai.cog.center.tool;

import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

/**
 * Tool 定义写入请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ToolUpsertRequest(
        String toolCode,
        String toolName,
        ToolProtocolType protocolType,
        SchemaDefinition requestSchema,
        SchemaDefinition responseSchema,
        String permissionScope,
        RiskLevel riskLevel,
        int timeoutMs,
        int retryMaxAttempts,
        String implRef,
        CommonStatus status
) {
}
