package cn.cyc.ai.cog.runtime.usage.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 租户用量额度账户。
 *
 * @param tenantCode    租户编码
 * @param balanceAmount 剩余额度
 * @param usedAmount    已用额度
 * @param enabled       是否启用额度拦截
 * @param updatedAt     更新时间
 * @author cyc
 */
public record UsageAccount(
        String tenantCode,
        BigDecimal balanceAmount,
        BigDecimal usedAmount,
        boolean enabled,
        Instant updatedAt
) {

    public UsageAccount {
        tenantCode = TenantContext.normalize(tenantCode);
        balanceAmount = normalizeAmount(balanceAmount);
        usedAmount = normalizeAmount(usedAmount);
        updatedAt = updatedAt == null ? Instant.now() : updatedAt;
    }

    /**
     * 扣减账户余额。
     *
     * @param costAmount 成本金额
     * @return 扣减后的账户
     */
    public UsageAccount deduct(BigDecimal costAmount) {
        BigDecimal normalizedCost = normalizeAmount(costAmount);
        return new UsageAccount(
                tenantCode,
                balanceAmount.subtract(normalizedCost),
                usedAmount.add(normalizedCost),
                enabled,
                Instant.now()
        );
    }

    /**
     * 判断余额是否足够。
     *
     * @param costAmount 成本金额
     * @return 是否足够
     */
    public boolean hasEnoughBalance(BigDecimal costAmount) {
        return balanceAmount.compareTo(normalizeAmount(costAmount)) >= 0;
    }

    private static BigDecimal normalizeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
