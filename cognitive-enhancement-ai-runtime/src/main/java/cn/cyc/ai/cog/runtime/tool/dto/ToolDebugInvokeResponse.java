package cn.cyc.ai.cog.runtime.tool.dto;

import cn.cyc.ai.cog.runtime.api.ToolInvocationResult;

/**
 * Tool 调试调用响应。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ToolDebugInvokeResponse(
        String traceId,
        String toolCode,
        String protocolType,
        String riskLevel,
        boolean mock,
        long latencyMs,
        ToolInvocationResult invocationResult
) {
}
