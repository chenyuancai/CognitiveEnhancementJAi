package cn.cyc.ai.cog.platform.billing.domain;

/**
 * 额度包领域对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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
