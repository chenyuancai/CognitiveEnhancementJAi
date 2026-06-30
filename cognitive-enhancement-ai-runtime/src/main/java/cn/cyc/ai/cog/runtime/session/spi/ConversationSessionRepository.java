package cn.cyc.ai.cog.runtime.session.spi;

import cn.cyc.ai.cog.runtime.session.domain.ConversationSession;

import java.util.List;
import java.util.Optional;

/**
 * 会话仓储接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface ConversationSessionRepository {

    /**
     * 按会话 ID 查询当前租户会话。
     *
     * @param sessionId 会话 ID
     * @return 会话记录
     */
    Optional<ConversationSession> findBySessionId(String sessionId);

    /**
     * 保存会话记录。
     *
     * @param session 会话记录
     */
    void save(ConversationSession session);

    /**
     * 按用户与能力编码查询会话列表（按更新时间倒序）。
     */
    List<ConversationSession> listByUserAndCapability(String userId, String capabilityCode);
}
