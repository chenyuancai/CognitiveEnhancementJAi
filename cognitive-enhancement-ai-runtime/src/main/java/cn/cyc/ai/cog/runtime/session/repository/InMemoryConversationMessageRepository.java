package cn.cyc.ai.cog.runtime.session.repository;

import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;
import cn.cyc.ai.cog.runtime.session.spi.ConversationMessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 内存会话消息仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryConversationMessageRepository implements ConversationMessageRepository {

    /**
     * 内存消息容器。
     */
    private final CopyOnWriteArrayList<ConversationMessage> messages = new CopyOnWriteArrayList<>();

    @Override
    public void save(ConversationMessage message) {
        messages.add(message);
    }

    @Override
    public List<ConversationMessage> findBySessionId(String sessionId) {
        String tenantCode = TenantContext.currentTenantCode();
        return messages.stream()
                .filter(message -> tenantCode.equals(message.tenantCode()))
                .filter(message -> sessionId.equals(message.sessionId()))
                .sorted(Comparator.comparing(ConversationMessage::recordedAt))
                .collect(Collectors.toList());
    }
}
