package cn.cyc.ai.cog.runtime.observation.mapper;

import cn.cyc.ai.cog.runtime.observation.entity.UsageRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 能力调用用量记录 Mapper。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface UsageRecordMapper extends BaseMapper<UsageRecordEntity> {
}
