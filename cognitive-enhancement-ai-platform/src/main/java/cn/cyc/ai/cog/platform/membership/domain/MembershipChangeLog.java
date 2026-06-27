package cn.cyc.ai.cog.platform.membership.domain;

import java.time.LocalDateTime;

public record MembershipChangeLog(
        Long id,
        Long tenantId,
        Long accountId,
        String fromLevelCode,
        String toLevelCode,
        String changeType,
        Long operatorId,
        String message,
        String remark,
        LocalDateTime createTime
) {
}
