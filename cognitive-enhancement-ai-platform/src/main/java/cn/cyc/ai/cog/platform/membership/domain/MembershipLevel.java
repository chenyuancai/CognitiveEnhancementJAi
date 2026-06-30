package cn.cyc.ai.cog.platform.membership.domain;

/**
 * MembershipLevel 记录
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record MembershipLevel(
        Long id,
        String levelCode,
        String levelName,
        String segment,
        Boolean isDefault,
        Integer sortNo,
        String status,
        String benefitsJson
) {
}
