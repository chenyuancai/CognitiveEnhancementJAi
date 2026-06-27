package cn.cyc.ai.cog.runtime.trace.mapper;

import cn.cyc.ai.cog.runtime.trace.entity.TraceSpanEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * TraceSpan Mapper。
 *
 * @author cyc
 */
@Mapper
public interface TraceSpanMapper extends BaseMapper<TraceSpanEntity> {
}
