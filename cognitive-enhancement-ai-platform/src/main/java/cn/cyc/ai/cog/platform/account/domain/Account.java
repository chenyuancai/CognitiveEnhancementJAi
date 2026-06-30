package cn.cyc.ai.cog.platform.account.domain;

/**
 * 商业账户领域对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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
