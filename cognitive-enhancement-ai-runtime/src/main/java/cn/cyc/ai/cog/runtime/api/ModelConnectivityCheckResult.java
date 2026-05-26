package cn.cyc.ai.cog.runtime.api;

/**
 * 模型连通性检查结果。
 *
 * @param reachable     是否可达
 * @param providerCode  模型提供方编码
 * @param modelCode     模型编码
 * @param latencyMs     调用耗时
 * @param mock          是否为 mock 调用
 * @param failureReason 失败原因
 * @param answerPreview 回答预览
 * @author cyc
 */
public record ModelConnectivityCheckResult(boolean reachable,
                                           String providerCode,
                                           String modelCode,
                                           long latencyMs,
                                           boolean mock,
                                           String failureReason,
                                           String answerPreview) {
}
