package cn.cyc.ai.cog.runtime.session.mapper;

import cn.cyc.ai.cog.runtime.session.entity.ConversationMessageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会话消息 Mapper。
 *
 * @author cyc
 */
@Mapper
public interface ConversationMessageMapper extends BaseMapper<ConversationMessageEntity> {
}
