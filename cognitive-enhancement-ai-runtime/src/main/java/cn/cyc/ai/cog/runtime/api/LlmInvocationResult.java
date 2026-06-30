package cn.cyc.ai.cog.runtime.api;

import java.util.Map;

/**
 * LLM 调用结果对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record LlmInvocationResult(String executorType,
                                  String providerCode,
                                  String modelCode,
                                  String promptCode,
                                  Object renderedPrompt,
                                  String answer,
                                  Map<String, Object> parameters,
                                  int inputTokenCount,
                                  int outputTokenCount,
                                  int totalTokenCount,
                                  long latencyMs,
                                  boolean mock) {
}
