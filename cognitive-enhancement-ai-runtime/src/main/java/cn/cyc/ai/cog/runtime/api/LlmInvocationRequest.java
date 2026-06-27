package cn.cyc.ai.cog.runtime.api;

import java.util.Map;

/**
 * LLM 调用请求对象。
 *
 * @param traceId        链路标识
 * @param capabilityCode 能力编码
 * @param agentCode      Agent 编码
 * @param providerCode   模型提供方编码
 * @param modelCode      模型编码
 * @param endpoint       模型调用地址
 * @param apiKey         提供商 API Key（来自 Center 元数据）
 * @param timeoutMs      调用超时时间
 * @param promptCode     Prompt 编码
 * @param promptInput    渲染后的提示词输入
 * @param parameters     扩展参数
 * @author cyc
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
