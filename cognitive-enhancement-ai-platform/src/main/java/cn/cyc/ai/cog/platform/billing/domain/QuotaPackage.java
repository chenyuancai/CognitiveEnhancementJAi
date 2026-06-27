package cn.cyc.ai.cog.platform.billing.domain;

/**
 * 额度包领域对象。
 *
 * @param id           套餐 ID
 * @param tenantId     租户 ID
 * @param packageCode  套餐编码
 * @param packageName  套餐名称
 * @param segment      客群分段（2C/2B/2G）
 * @param tokenAmount  Token 数量
 * @param priceFen     售价（分）
 * @param validDays    有效天数
 * @param status       状态
 */
public record QuotaPackage(
        Long id,
        Long tenantId,
        String packageCode,
        String packageName,
        String segment,
        Long tokenAmount,
        Long priceFen,
        Integer validDays,
        String status
) {
}
