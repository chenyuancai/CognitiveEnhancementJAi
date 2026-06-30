package cn.cyc.ai.cog.platform.quota.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 额度MemberAlloc实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_mbr_quota_member_alloc")
public class QuotaMemberAllocEntity extends BaseEntity {

    /** 账户ID */
    private Long accountId;
    /** 用户 ID */
    private Long userId;
    /** allocatedAmount。 */
    private Long allocatedAmount;
    /** usedAmount。 */
    private Long usedAmount;
}
