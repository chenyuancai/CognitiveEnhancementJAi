package cn.cyc.ai.cog.platform.quota.mapper;

import cn.cyc.ai.cog.platform.quota.entity.QuotaAccountEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 额度账户数据访问 Mapper
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface QuotaAccountMapper extends BaseMapper<QuotaAccountEntity> {
}
