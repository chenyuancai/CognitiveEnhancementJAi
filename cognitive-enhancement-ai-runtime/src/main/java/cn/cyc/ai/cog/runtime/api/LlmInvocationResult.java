package cn.cyc.ai.cog.runtime.api;

import java.util.Map;

/**
 * LLM 调用结果对象。
 *
 * @param executorType   执行器类型
 * @param providerCode   模型提供方编码
 * @param modelCode      模型编码
 * @param promptCode     Prompt 编码
 * @param renderedPrompt 渲染后的提示词
 * @param answer            模型回答内容
 * @param parameters        透传的执行参数
 * @param inputTokenCount   输入 token 数
 * @param outputTokenCount  输出 token 数
 * @param totalTokenCount   总 token 数
 * @param latencyMs         调用耗时（毫秒）
 * @param mock              是否为 mock 返回
 * @author cyc
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
