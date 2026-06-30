package cn.cyc.ai.cog.runtime.api;

import java.util.Map;

/**
 * Tool 调用请求对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ToolInvocationRequest(String traceId,
                                    String capabilityCode,
                                    String agentCode,
                                    String toolCode,
                                    String protocolType,
                                    Object input,
                                    Map<String, Object> parameters) {
}
