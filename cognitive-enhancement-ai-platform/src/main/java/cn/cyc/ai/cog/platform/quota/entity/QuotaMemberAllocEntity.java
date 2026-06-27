package cn.cyc.ai.cog.platform.quota.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_mbr_quota_member_alloc")
public class QuotaMemberAllocEntity extends BaseEntity {

    private Long accountId;
    private Long userId;
    private Long allocatedAmount;
    private Long usedAmount;
}
