package cn.cyc.ai.cog.runtime.session.repository;

import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;
import cn.cyc.ai.cog.runtime.session.spi.ConversationMessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 内存会话消息仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryConversationMessageRepository implements ConversationMessageRepository {

    /**
     * 内存消息容器。
     */
    private final CopyOnWriteArrayList<ConversationMessage> messages = new CopyOnWriteArrayList<>();

    /**
     * 执行save。
     *
     * @param message 消息
     */
    @Override
    public void save(ConversationMessage message) {
        messages.add(message);
    }

    /**
     * 查找人会话ID。
     *
     * @param sessionId 会话 ID
     * @return 查找结果
     */
    @Override
    public List<ConversationMessage> findBySessionId(String sessionId) {
        String tenantCode = TenantContext.currentTenantCode();
        return messages.stream()
                .filter(message -> tenantCode.equals(message.tenantCode()))
                .filter(message -> sessionId.equals(message.sessionId()))
                .sorted(Comparator.comparing(ConversationMessage::recordedAt))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ConversationMessage> findLatestBySessionId(String sessionId) {
        return findBySessionId(sessionId).stream().reduce((first, second) -> second);
    }
}
