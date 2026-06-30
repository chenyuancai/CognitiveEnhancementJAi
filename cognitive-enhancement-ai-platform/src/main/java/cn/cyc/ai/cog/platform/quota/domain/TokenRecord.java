package cn.cyc.ai.cog.platform.quota.domain;

import java.time.LocalDateTime;

/**
 * 令牌记录
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
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
