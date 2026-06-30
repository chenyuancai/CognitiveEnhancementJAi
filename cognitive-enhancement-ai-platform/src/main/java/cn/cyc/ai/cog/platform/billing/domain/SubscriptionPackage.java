package cn.cyc.ai.cog.platform.billing.domain;

/**
 * 订阅套餐领域对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record SubscriptionPackage(
        Long id,
        Long tenantId,
        String packageCode,
        String packageName,
        String segment,
        Long levelId,
        String billingPeriod,
        Integer periodCount,
        Long priceFen,
        Long originalPriceFen,
        Long cycleTokenQuota,
        Integer seatCount,
        String saleMode,
        Boolean requireContract,
        String status,
        String snapshotJson,
        Integer trialDays
) {
}
