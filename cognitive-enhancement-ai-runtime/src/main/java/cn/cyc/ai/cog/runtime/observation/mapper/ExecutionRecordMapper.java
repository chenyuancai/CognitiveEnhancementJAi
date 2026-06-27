package cn.cyc.ai.cog.runtime.observation.mapper;

import cn.cyc.ai.cog.runtime.observation.entity.ExecutionRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 能力执行链路摘要 Mapper。
 *
 * @author cyc
 */
@Mapper
public interface ExecutionRecordMapper extends BaseMapper<ExecutionRecordEntity> {
}
