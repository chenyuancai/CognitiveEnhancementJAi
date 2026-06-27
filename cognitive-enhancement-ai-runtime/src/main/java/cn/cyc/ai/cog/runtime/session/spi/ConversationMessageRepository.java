package cn.cyc.ai.cog.runtime.session.spi;

import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;

import java.util.List;

/**
 * 会话消息仓储接口。
 *
 * @author cyc
 */
public interface ConversationMessageRepository {

    /**
     * 保存会话消息。
     *
     * @param message 会话消息
     */
    void save(ConversationMessage message);

    /**
     * 按会话 ID 查询当前租户消息列表，按记录时间升序。
     *
     * @param sessionId 会话 ID
     * @return 消息列表
     */
    List<ConversationMessage> findBySessionId(String sessionId);
}
