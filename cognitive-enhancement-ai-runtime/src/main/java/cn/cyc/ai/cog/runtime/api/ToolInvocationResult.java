package cn.cyc.ai.cog.runtime.api;

import java.util.Map;

/**
 * Tool 调用结果对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ToolInvocationResult(String executorType,
                                   String toolCode,
                                   String protocolType,
                                   String permissionScope,
                                   String riskLevel,
                                   Object input,
                                   Map<String, Object> parameters,
                                   Object toolPayload,
                                   boolean mock) {
}
