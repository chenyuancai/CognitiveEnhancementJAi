package cn.cyc.ai.cog.runtime.api;

import java.time.Instant;
import java.util.List;

/**
 * 模型失败原因聚合结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ModelFailureSummaryResult(String reason,
                                        int count,
                                        Instant latestOccurredAt,
                                        List<String> affectedModelCodes) {
}
