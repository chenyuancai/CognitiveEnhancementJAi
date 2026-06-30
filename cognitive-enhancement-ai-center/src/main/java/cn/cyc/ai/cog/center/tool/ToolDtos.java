package cn.cyc.ai.cog.center.tool;

import cn.cyc.ai.cog.center.common.SchemaDto;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;

/**
 * Tool DTO 定义。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class ToolDtos {

    /**
     * 创建ToolDtos。
     */
    private ToolDtos() {
    }

    /**
     * 创建请求
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
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

    /**
     * 更新请求
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
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

    /**
     * Result 记录
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
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
