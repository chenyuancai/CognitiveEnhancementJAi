package cn.cyc.ai.cog.runtime.api;

import java.time.Instant;

/**
 * Runtime 模型状态摘要结果。
 *
 * @param providerCode    模型提供方编码
 * @param providerName    模型提供方名称
 * @param modelCode       模型编码
 * @param modelName       模型名称
 * @param modelType       模型类型
 * @param status          模型状态
 * @param healthStatus    模型健康状态
 * @param routePriority   路由优先级
 * @param fallbackModelCode 降级模型编码
 * @param hasCheckRecord  是否存在检查记录
 * @param reachable       最近一次检查是否可达
 * @param latencyMs       最近一次检查耗时
 * @param mock            最近一次检查是否为 mock
 * @param failureReason   最近一次失败原因
 * @param answerPreview   最近一次回答预览
 * @param lastCheckedAt   最近一次检查时间
 * @param lastSuccessAt   最近一次成功时间
 * @param lastFailureAt   最近一次失败时间
 * @param consecutiveFailureCount 连续失败次数
 * @author cyc
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
