package cn.cyc.ai.cog.runtime.domain;

import java.time.Instant;

/**
 * 模型连通性检查记录。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ModelCheckRecord(String traceId,
                               String providerCode,
                               String modelCode,
                               boolean reachable,
                               long latencyMs,
                               boolean mock,
                               String failureReason,
                               String answerPreview,
                               Instant recordedAt) {
}
