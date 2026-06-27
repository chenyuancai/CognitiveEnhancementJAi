package cn.cyc.ai.cog.runtime.tool.dto;

import cn.cyc.ai.cog.runtime.api.ToolInvocationResult;

/**
 * Tool 调试调用响应。
 *
 * @param traceId          链路追踪 ID
 * @param toolCode         Tool 编码
 * @param protocolType     Tool 协议类型
 * @param riskLevel        风险等级
 * @param mock             是否为 mock 返回
 * @param latencyMs        调用耗时
 * @param invocationResult ToolRuntime 调用结果
 * @author cyc
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
