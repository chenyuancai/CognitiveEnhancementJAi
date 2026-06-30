package cn.cyc.ai.cog.platform.billing.domain;

import java.time.LocalDateTime;

/**
 * 订单领域对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record Order(
        Long id,
        Long tenantId,
        String orderNo,
        Long accountId,
        Long buyerUserId,
        String orderType,
        Long packageId,
        String packageSnapshotJson,
        Long amountFen,
        String currency,
        String status,
        String payChannel,
        LocalDateTime payTime,
        LocalDateTime fulfillTime,
        String idempotencyKey,
        Long refundAmountFen,
        LocalDateTime refundTime,
        String remark
) {
}
