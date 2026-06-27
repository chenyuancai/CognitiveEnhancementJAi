package cn.cyc.ai.cog.runtime.session.repository;

import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.session.domain.ConversationSession;
import cn.cyc.ai.cog.runtime.session.spi.ConversationSessionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存会话仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryConversationSessionRepository implements ConversationSessionRepository {

    /**
     * 按租户与会话 ID 保存会话。
     */
    private final Map<String, ConversationSession> sessions = new ConcurrentHashMap<>();

    @Override
    public Optional<ConversationSession> findBySessionId(String sessionId) {
        String tenantCode = TenantContext.currentTenantCode();
        return Optional.ofNullable(sessions.get(sessionKey(tenantCode, sessionId)));
    }

    @Override
    public void save(ConversationSession session) {
        sessions.put(sessionKey(session.tenantCode(), session.sessionId()), session);
    }

    private String sessionKey(String tenantCode, String sessionId) {
        return TenantContext.normalize(tenantCode) + ":" + sessionId;
    }
}
