package cn.cyc.ai.cog.runtime.usage.mapper;

import cn.cyc.ai.cog.runtime.usage.entity.UsageAccountEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户用量额度账户 Mapper。
 *
 * @author cyc
 */
@Mapper
public interface UsageAccountMapper extends BaseMapper<UsageAccountEntity> {

    /**
     * 按租户编码插入或更新账户。
     *
     * @param entity 账户实体
     */
    @Insert("""
            INSERT INTO qz_rt_usage_account (
                tenant_id,
                balance_amount,
                used_amount,
                enabled,
                updated_at
            ) VALUES (
                #{tenantId},
                #{balanceAmount},
                #{usedAmount},
                #{enabled},
                #{updatedAt}
            )
            ON DUPLICATE KEY UPDATE
                balance_amount = VALUES(balance_amount),
                used_amount = VALUES(used_amount),
                enabled = VALUES(enabled),
                updated_at = VALUES(updated_at),
                update_time = CURRENT_TIMESTAMP(3)
            """)
    void saveOrUpdateByTenantCode(UsageAccountEntity entity);
}
