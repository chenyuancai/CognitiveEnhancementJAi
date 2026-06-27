package cn.cyc.ai.cog.runtime.session.mapper;

import cn.cyc.ai.cog.runtime.session.entity.ConversationSessionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会话 Mapper。
 *
 * @author cyc
 */
@Mapper
public interface ConversationSessionMapper extends BaseMapper<ConversationSessionEntity> {
}
