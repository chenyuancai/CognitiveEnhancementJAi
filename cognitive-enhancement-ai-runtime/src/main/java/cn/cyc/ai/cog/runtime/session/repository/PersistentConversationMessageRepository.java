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
import java.util.Optional;

/**
 * 持久化会话消息仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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

    /**
     * 执行save。
     *
     * @param message 消息
     */
    @Override
    public void save(ConversationMessage message) {
        conversationMessageMapper.insert(toEntity(message));
        log.debug("持久化会话消息, sessionId={}, messageId={}, role={}",
                message.sessionId(), message.messageId(), message.role());
    }

    /**
     * 查找人会话ID。
     *
     * @param sessionId 会话 ID
     * @return 查找结果
     */
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

    @Override
    public Optional<ConversationMessage> findLatestBySessionId(String sessionId) {
        LambdaQueryWrapper<ConversationMessageEntity> queryWrapper = new LambdaQueryWrapper<ConversationMessageEntity>()
                .eq(ConversationMessageEntity::getTenantId, TenantContext.currentTenantId())
                .eq(ConversationMessageEntity::getSessionId, sessionId)
                .orderByDesc(ConversationMessageEntity::getRecordedAt)
                .orderByDesc(ConversationMessageEntity::getId)
                .last("LIMIT 1");
        ConversationMessageEntity entity = conversationMessageMapper.selectOne(queryWrapper);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    /**
     * 转换为实体。
     *
     * @param message 消息
     * @return 转换结果
     */
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

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
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
