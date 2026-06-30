package cn.cyc.ai.cog.platform.billing.mapper;

import cn.cyc.ai.cog.platform.billing.entity.QuotaPackageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 额度Package数据访问 Mapper
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface QuotaPackageMapper extends BaseMapper<QuotaPackageEntity> {
}
