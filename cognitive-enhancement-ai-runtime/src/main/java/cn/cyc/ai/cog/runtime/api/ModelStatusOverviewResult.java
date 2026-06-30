package cn.cyc.ai.cog.runtime.api;

import java.time.Instant;
import java.util.List;

/**
 * 模型状态总览结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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
