package cn.cyc.ai.cog.runtime.api;

import java.util.Map;

/**
 * LLM 调用请求对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record LlmInvocationRequest(String traceId,
                                   String capabilityCode,
                                   String agentCode,
                                   String providerCode,
                                   String modelCode,
                                   String endpoint,
                                   String apiKey,
                                   int timeoutMs,
                                   String promptCode,
                                   Object promptInput,
                                   Map<String, Object> parameters) {
}
