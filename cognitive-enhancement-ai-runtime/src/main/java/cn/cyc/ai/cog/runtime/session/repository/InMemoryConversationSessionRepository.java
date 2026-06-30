package cn.cyc.ai.cog.runtime.session.repository;

import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.session.domain.ConversationSession;
import cn.cyc.ai.cog.runtime.session.spi.ConversationSessionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 内存会话仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryConversationSessionRepository implements ConversationSessionRepository {

    /**
     * 按租户与会话 ID 保存会话。
     */
    private final Map<String, ConversationSession> sessions = new ConcurrentHashMap<>();

    /**
     * 查找人会话ID。
     *
     * @param sessionId 会话 ID
     * @return 查找结果
     */
    @Override
    public Optional<ConversationSession> findBySessionId(String sessionId) {
        String tenantCode = TenantContext.currentTenantCode();
        return Optional.ofNullable(sessions.get(sessionKey(tenantCode, sessionId)));
    }

    /**
     * 执行save。
     *
     * @param session 会话
     */
    @Override
    public void save(ConversationSession session) {
        sessions.put(sessionKey(session.tenantCode(), session.sessionId()), session);
    }

    @Override
    public List<ConversationSession> listByUserAndCapability(String userId, String capabilityCode) {
        String tenantCode = TenantContext.currentTenantCode();
        return sessions.values().stream()
                .filter(session -> tenantCode.equals(session.tenantCode()))
                .filter(session -> userId.equals(session.userId()))
                .filter(session -> capabilityCode == null || capabilityCode.equals(session.capabilityCode()))
                .sorted(Comparator.comparing(ConversationSession::updatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 执行会话键。
     *
     * @param tenantCode 租户编码
     * @param sessionId 会话 ID
     * @return 执行结果
     */
    private String sessionKey(String tenantCode, String sessionId) {
        return TenantContext.normalize(tenantCode) + ":" + sessionId;
    }
}
