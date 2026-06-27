package cn.cyc.ai.cog.runtime.usage.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 租户用量额度账户实体。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_rt_usage_account")
public class UsageAccountEntity extends BaseEntity {

    /**
     * 租户 ID。
     */
    private Long tenantId;

    /**
     * 剩余额度。
     */
    private BigDecimal balanceAmount;

    /**
     * 已用额度。
     */
    private BigDecimal usedAmount;

    /**
     * 是否启用额度拦截。
     */
    private Boolean enabled;

    /**
     * 账户更新时间。
     */
    private Instant updatedAt;
}
