package cn.cyc.ai.cog.platform.account.domain;

/**
 * 商业账户领域对象。
 *
 * @param id           账户 ID
 * @param tenantId     租户 ID
 * @param accountType  账户类型（如 INDIVIDUAL、ENTERPRISE）
 * @param segment      客群分段（2C/2B/2G）
 * @param displayName  展示名称
 * @param ownerUserId  负责人用户 ID
 * @param status       状态
 */
public record Account(
        Long id,
        Long tenantId,
        String accountType,
        String segment,
        String displayName,
        Long ownerUserId,
        String status
) {
}
