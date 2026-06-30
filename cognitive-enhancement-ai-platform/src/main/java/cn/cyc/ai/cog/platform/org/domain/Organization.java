package cn.cyc.ai.cog.platform.org.domain;

import java.time.LocalDateTime;

/**
 * 组织领域对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record Organization(
        Long id,
        Long tenantId,
        Long accountId,
        String orgType,
        String orgName,
        String unifiedSocialCode,
        Integer seatLimit,
        String contactName,
        String contactPhone,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
