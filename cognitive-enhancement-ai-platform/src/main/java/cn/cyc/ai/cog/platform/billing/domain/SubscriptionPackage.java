package cn.cyc.ai.cog.platform.billing.domain;

/**
 * 订阅套餐领域对象。
 *
 * @param id                套餐 ID
 * @param tenantId          租户 ID
 * @param packageCode       套餐编码
 * @param packageName       套餐名称
 * @param segment           客群分段（2C/2B/2G）
 * @param levelId           关联会员等级 ID
 * @param billingPeriod     计费周期（如 MONTH）
 * @param periodCount       周期数量
 * @param priceFen          售价（分）
 * @param originalPriceFen  原价（分）
 * @param cycleTokenQuota   每周期 Token 额度
 * @param seatCount         席位数
 * @param saleMode          销售模式
 * @param requireContract   是否需签约
 * @param status            状态
 * @param snapshotJson      套餐快照 JSON
 * @param trialDays         试用期天数（0=无）
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
