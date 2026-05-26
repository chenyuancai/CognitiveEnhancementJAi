package cn.cyc.ai.cog.runtime.api;

import java.time.Instant;
import java.util.List;

/**
 * 模型状态总览结果。
 *
 * @param totalModels      模型总数
 * @param enabledModels    启用模型数
 * @param disabledModels   禁用模型数
 * @param checkedModels    已检查模型数
 * @param reachableModels  可达模型数
 * @param unreachableModels 不可达模型数
 * @param uncheckedModels  未检查模型数
 * @param lastCheckedAt    最近检查时间
 * @param lastSuccessAt    最近成功时间
 * @param lastFailureAt    最近失败时间
 * @param failureSummaries 失败原因聚合列表
 * @author cyc
 */
public record ModelStatusOverviewResult(int totalModels,
                                        int enabledModels,
                                        int disabledModels,
                                        int checkedModels,
                                        int reachableModels,
                                        int unreachableModels,
                                        int uncheckedModels,
                                        Instant lastCheckedAt,
                                        Instant lastSuccessAt,
                                        Instant lastFailureAt,
                                        List<ModelFailureSummaryResult> failureSummaries) {
}
