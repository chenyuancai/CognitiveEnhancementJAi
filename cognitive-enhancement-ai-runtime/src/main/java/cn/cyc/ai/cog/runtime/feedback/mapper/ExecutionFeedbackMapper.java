package cn.cyc.ai.cog.runtime.feedback.mapper;

import cn.cyc.ai.cog.runtime.feedback.entity.ExecutionFeedbackEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 执行反馈 Mapper。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface ExecutionFeedbackMapper extends BaseMapper<ExecutionFeedbackEntity> {
}
