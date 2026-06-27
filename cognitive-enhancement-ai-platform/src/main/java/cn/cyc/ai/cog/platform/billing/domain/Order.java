package cn.cyc.ai.cog.platform.billing.domain;

import java.time.LocalDateTime;

/**
 * 订单领域对象。
 *
 * @param id                  订单 ID
 * @param tenantId            租户 ID
 * @param orderNo             订单号
 * @param accountId           商业账户 ID
 * @param buyerUserId         购买用户 ID
 * @param orderType           订单类型（SUBSCRIPTION / QUOTA）
 * @param packageId           套餐 ID
 * @param packageSnapshotJson 套餐快照 JSON
 * @param amountFen           金额（分）
 * @param currency            币种
 * @param status              订单状态
 * @param payChannel          支付渠道
 * @param payTime             支付时间
 * @param fulfillTime         发放时间
 * @param idempotencyKey      幂等键
 * @param refundAmountFen     退款金额（分）
 * @param refundTime          退款时间
 * @param remark              备注
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
