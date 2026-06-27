package cn.cyc.ai.cog.platform.billing.domain;

import java.time.LocalDateTime;

/**
 * 资金流水领域对象。
 *
 * @param id              流水 ID
 * @param tenantId        租户 ID
 * @param accountId       商业账户 ID
 * @param orderId         关联订单 ID
 * @param recordType      流水类型（如 PAYMENT、REFUND）
 * @param amountFen       金额（分）
 * @param balanceAfterFen 变更后余额（分）
 * @param remark          备注
 * @param createTime      创建时间
 */
public record FinancialRecord(
        Long id,
        Long tenantId,
        Long accountId,
        Long orderId,
        String recordType,
        String message,
        Long amountFen,
        Long balanceAfterFen,
        String remark,
        LocalDateTime createTime
) {
}
