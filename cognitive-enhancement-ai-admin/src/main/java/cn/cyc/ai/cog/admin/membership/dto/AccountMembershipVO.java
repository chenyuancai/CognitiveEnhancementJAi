package cn.cyc.ai.cog.admin.membership.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 账户会员视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AccountMembershipVO {

    /** 主键 ID */
    private Long id;
    /** 租户 ID */
    private Long tenantId;
    /** 账户ID */
    private Long accountId;
    /** 等级ID */
    private Long levelId;
    /** 等级编码。 */
    private String levelCode;
    /** expireAt。 */
    private LocalDateTime expireAt;
    /** 来源。 */
    private String source;
}
