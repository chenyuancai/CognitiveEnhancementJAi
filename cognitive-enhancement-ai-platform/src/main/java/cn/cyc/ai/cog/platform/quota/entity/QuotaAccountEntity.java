package cn.cyc.ai.cog.platform.quota.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 额度账户（映射 qz_mbr_quota_account）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_mbr_quota_account")
public class QuotaAccountEntity extends BaseEntity {

    private Long accountId;
    private Long cycleRemaining;
    private Long cycleTotal;
    private LocalDateTime cycleResetAt;
    private Long giftRemaining;
    private Long giftTotal;
    private Long topupRemaining;
    private Long topupTotal;
}
