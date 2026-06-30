package cn.cyc.ai.cog.platform.membership.domain;

import java.time.LocalDateTime;

/**
 * MembershipChangeLog 记录
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
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
