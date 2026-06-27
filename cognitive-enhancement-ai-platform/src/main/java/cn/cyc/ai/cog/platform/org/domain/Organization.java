package cn.cyc.ai.cog.platform.org.domain;

import java.time.LocalDateTime;

/**
 * 组织领域对象。
 *
 * @param id                 组织 ID
 * @param tenantId           租户 ID
 * @param accountId          关联商业账户 ID
 * @param orgType            组织类型
 * @param orgName            组织名称
 * @param unifiedSocialCode  统一社会信用代码
 * @param seatLimit          席位上限
 * @param contactName        联系人
 * @param contactPhone       联系电话
 * @param createTime         创建时间
 * @param updateTime         更新时间
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
