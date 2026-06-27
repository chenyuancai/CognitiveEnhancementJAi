package cn.cyc.ai.cog.runtime.session.repository;

import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.security.TenantIds;
import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;
import cn.cyc.ai.cog.runtime.session.domain.MessageRole;
import cn.cyc.ai.cog.runtime.session.entity.ConversationMessageEntity;
import cn.cyc.ai.cog.runtime.session.mapper.ConversationMessageMapper;
import cn.cyc.ai.cog.runtime.session.spi.ConversationMessageRepository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 持久化会话消息仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentConversationMessageRepository implements ConversationMessageRepository {

    /**
     * 仓储日志。
     */
    private static final Logger log = LoggerFactory.getLogger(PersistentConversationMessageRepository.class);

    /**
     * 会话消息 Mapper。
     */
    private final ConversationMessageMapper conversationMessageMapper;

    /**
     * 构造持久化会话消息仓储。
     *
     * @param conversationMessageMapper 会话消息 Mapper
     */
    public PersistentConversationMessageRepository(ConversationMessageMapper conversationMessageMapper) {
        this.conversationMessageMapper = conversationMessageMapper;
    }

    @Override
    public void save(ConversationMessage message) {
        conversationMessageMapper.insert(toEntity(message));
        log.debug("持久化会话消息, sessionId={}, messageId={}, role={}",
                message.sessionId(), message.messageId(), message.role());
    }

    @Override
    public List<ConversationMessage> findBySessionId(String sessionId) {
        LambdaQueryWrapper<ConversationMessageEntity> queryWrapper = new LambdaQueryWrapper<ConversationMessageEntity>()
                .eq(ConversationMessageEntity::getTenantId, TenantContext.currentTenantId())
                .eq(ConversationMessageEntity::getSessionId, sessionId)
                .orderByAsc(ConversationMessageEntity::getRecordedAt)
                .orderByAsc(ConversationMessageEntity::getId);
        return conversationMessageMapper.selectList(queryWrapper).stream()
                .map(this::toDomain)
                .toList();
    }

    private ConversationMessageEntity toEntity(ConversationMessage message) {
        ConversationMessageEntity entity = new ConversationMessageEntity();
        entity.setTenantId(TenantIds.resolveId(message.tenantCode()));
        entity.setMessageId(message.messageId());
        entity.setSessionId(message.sessionId());
        entity.setRole(message.role().name());
        entity.setContent(message.content());
        entity.setTraceId(message.traceId());
        entity.setRecordedAt(message.recordedAt());
        return entity;
    }

    private ConversationMessage toDomain(ConversationMessageEntity entity) {
        return new ConversationMessage(
                TenantIds.toCode(entity.getTenantId()),
                entity.getMessageId(),
                entity.getSessionId(),
                MessageRole.valueOf(entity.getRole()),
                entity.getContent(),
                entity.getTraceId(),
                entity.getRecordedAt()
        );
    }
}
