package cn.cyc.ai.cog.platform.tutoring.mapper;

import cn.cyc.ai.cog.platform.tutoring.entity.ConversationSummaryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会话摘要 Mapper。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Mapper
public interface ConversationSummaryMapper extends BaseMapper<ConversationSummaryEntity> {
}
