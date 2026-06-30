package cn.cyc.ai.cog.platform.quota.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 额度账户（映射 qz_mbr_quota_account）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_mbr_quota_account")
public class QuotaAccountEntity extends BaseEntity {

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
