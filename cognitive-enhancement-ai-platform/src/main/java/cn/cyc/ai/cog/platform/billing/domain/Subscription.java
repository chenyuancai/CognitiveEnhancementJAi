package cn.cyc.ai.cog.platform.billing.domain;

import java.time.LocalDateTime;

/**
 * 订阅记录领域对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record Subscription(
        Long id,
        Long tenantId,
        Long accountId,
        Long orderId,
        Long packageId,
        String levelCode,
        String status,
        String phase,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Boolean autoRenew,
        String packageSnapshotJson
) {
}
