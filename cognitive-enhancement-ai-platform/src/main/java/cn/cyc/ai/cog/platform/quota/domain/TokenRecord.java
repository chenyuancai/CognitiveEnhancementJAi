package cn.cyc.ai.cog.platform.quota.domain;

import java.time.LocalDateTime;

public record TokenRecord(
        Long id,
        Long tenantId,
        Long accountId,
        Long memberUserId,
        String recordType,
        String bucket,
        Long deltaAmount,
        Long balanceAfter,
        String bizType,
        String bizId,
        String idempotencyKey,
        String message,
        LocalDateTime createTime
) {
}
