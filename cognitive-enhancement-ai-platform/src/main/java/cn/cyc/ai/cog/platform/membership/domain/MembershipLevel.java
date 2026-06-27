package cn.cyc.ai.cog.platform.membership.domain;

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
