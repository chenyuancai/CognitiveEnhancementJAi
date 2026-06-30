package cn.cyc.ai.cog.platform.billing.domain;

import java.time.LocalDateTime;

/**
 * 资金流水领域对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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
