package cn.cyc.ai.cog.platform.quota.mapper;

import cn.cyc.ai.cog.platform.quota.entity.TokenRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 令牌Record数据访问 Mapper
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface TokenRecordMapper extends BaseMapper<TokenRecordEntity> {
}
