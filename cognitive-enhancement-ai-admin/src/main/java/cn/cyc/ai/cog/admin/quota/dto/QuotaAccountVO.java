package cn.cyc.ai.cog.admin.quota.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 额度账户视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class QuotaAccountVO {

    /** 主键 ID */
    private Long id;
    /** 租户 ID */
    private Long tenantId;
    /** 账户ID */
    private Long accountId;
    /** cycleRemaining。 */
    private Long cycleRemaining;
    /** cycle总数。 */
    private Long cycleTotal;
    /** cycleResetAt。 */
    private LocalDateTime cycleResetAt;
    /** giftRemaining。 */
    private Long giftRemaining;
    /** gift总数。 */
    private Long giftTotal;
    /** topupRemaining。 */
    private Long topupRemaining;
    /** topup总数。 */
    private Long topupTotal;
}
