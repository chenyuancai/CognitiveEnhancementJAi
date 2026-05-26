package cn.cyc.ai.cog.runtime.api;

import java.time.Instant;
import java.util.List;

/**
 * 模型失败原因聚合结果。
 *
 * @param reason             失败原因
 * @param count              命中次数
 * @param latestOccurredAt   最近发生时间
 * @param affectedModelCodes 受影响模型编码列表
 * @author cyc
 */
public record ModelFailureSummaryResult(String reason,
                                        int count,
                                        Instant latestOccurredAt,
                                        List<String> affectedModelCodes) {
}
