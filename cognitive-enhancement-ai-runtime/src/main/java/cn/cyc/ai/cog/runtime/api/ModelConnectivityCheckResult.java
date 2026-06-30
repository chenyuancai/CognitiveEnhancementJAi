package cn.cyc.ai.cog.runtime.api;

/**
 * 模型连通性检查结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ModelConnectivityCheckResult(boolean reachable,
                                           String providerCode,
                                           String modelCode,
                                           long latencyMs,
                                           boolean mock,
                                           String failureReason,
                                           String answerPreview) {
}
