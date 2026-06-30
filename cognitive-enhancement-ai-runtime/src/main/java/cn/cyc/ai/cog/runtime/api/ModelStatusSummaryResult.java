package cn.cyc.ai.cog.runtime.api;

import java.time.Instant;

/**
 * Runtime 模型状态摘要结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ModelStatusSummaryResult(String providerCode,
                                       String providerName,
                                       String modelCode,
                                       String modelName,
                                       String modelType,
                                       String status,
                                       String healthStatus,
                                       int routePriority,
                                       String fallbackModelCode,
                                       boolean hasCheckRecord,
                                       Boolean reachable,
                                       Long latencyMs,
                                       Boolean mock,
                                       String failureReason,
                                       String answerPreview,
                                       Instant lastCheckedAt,
                                       Instant lastSuccessAt,
                                       Instant lastFailureAt,
                                       int consecutiveFailureCount) {
}
