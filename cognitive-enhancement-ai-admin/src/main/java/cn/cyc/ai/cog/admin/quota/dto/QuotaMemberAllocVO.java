package cn.cyc.ai.cog.admin.quota.dto;

import lombok.Data;

/**
 * 额度MemberAlloc视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class QuotaMemberAllocVO {

    /** 主键 ID */
    private Long id;
    /** 账户ID */
    private Long accountId;
    /** 用户 ID */
    private Long userId;
    /** allocatedAmount。 */
    private Long allocatedAmount;
    /** usedAmount。 */
    private Long usedAmount;
}
