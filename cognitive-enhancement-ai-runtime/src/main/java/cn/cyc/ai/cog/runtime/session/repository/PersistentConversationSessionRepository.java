package cn.cyc.ai.cog.runtime.session.repository;

import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.security.TenantIds;
import cn.cyc.ai.cog.runtime.session.domain.ConversationSession;
import cn.cyc.ai.cog.runtime.session.domain.SessionStatus;
import cn.cyc.ai.cog.runtime.session.entity.ConversationSessionEntity;
import cn.cyc.ai.cog.runtime.session.mapper.ConversationSessionMapper;
import cn.cyc.ai.cog.runtime.session.spi.ConversationSessionRepository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 持久化会话仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentConversationSessionRepository implements ConversationSessionRepository {

    /**
     * 仓储日志。
     */
    private static final Logger log = LoggerFactory.getLogger(PersistentConversationSessionRepository.class);

    /**
     * 会话 Mapper。
     */
    private final ConversationSessionMapper conversationSessionMapper;

    /**
     * 构造持久化会话仓储。
     *
     * @param conversationSessionMapper 会话 Mapper
     */
    public PersistentConversationSessionRepository(ConversationSessionMapper conversationSessionMapper) {
        this.conversationSessionMapper = conversationSessionMapper;
    }

    /**
     * 查找人会话ID。
     *
     * @param sessionId 会话 ID
     * @return 查找结果
     */
    @Override
    public Optional<ConversationSession> findBySessionId(String sessionId) {
        LambdaQueryWrapper<ConversationSessionEntity> queryWrapper = new LambdaQueryWrapper<ConversationSessionEntity>()
                .eq(ConversationSessionEntity::getTenantId, TenantContext.currentTenantId())
                .eq(ConversationSessionEntity::getSessionId, sessionId);
        return Optional.ofNullable(conversationSessionMapper.selectOne(queryWrapper))
                .map(this::toDomain);
    }

    /**
     * 执行save。
     *
     * @param session 会话
     */
    @Override
    public void save(ConversationSession session) {
        LambdaQueryWrapper<ConversationSessionEntity> queryWrapper = new LambdaQueryWrapper<ConversationSessionEntity>()
                .eq(ConversationSessionEntity::getTenantId, TenantIds.resolveId(session.tenantCode()))
                .eq(ConversationSessionEntity::getSessionId, session.sessionId());
        ConversationSessionEntity existing = conversationSessionMapper.selectOne(queryWrapper);
        ConversationSessionEntity entity = toEntity(session);
        if (existing == null) {
            conversationSessionMapper.insert(entity);
            log.debug("持久化新建会话, sessionId={}, userId={}", session.sessionId(), session.userId());
            return;
        }
        entity.setId(existing.getId());
        conversationSessionMapper.updateById(entity);
        log.debug("持久化更新会话, sessionId={}, userId={}", session.sessionId(), session.userId());
    }

    @Override
    public List<ConversationSession> listByUserAndCapability(String userId, String capabilityCode) {
        LambdaQueryWrapper<ConversationSessionEntity> queryWrapper = new LambdaQueryWrapper<ConversationSessionEntity>()
                .eq(ConversationSessionEntity::getTenantId, TenantContext.currentTenantId())
                .eq(ConversationSessionEntity::getUserId, userId)
                .eq(capabilityCode != null, ConversationSessionEntity::getCapabilityCode, capabilityCode)
                .orderByDesc(ConversationSessionEntity::getUpdatedAt);
        return conversationSessionMapper.selectList(queryWrapper).stream().map(this::toDomain).toList();
    }

    /**
     * 转换为实体。
     *
     * @param session 会话
     * @return 转换结果
     */
    private ConversationSessionEntity toEntity(ConversationSession session) {
        ConversationSessionEntity entity = new ConversationSessionEntity();
        entity.setTenantId(TenantIds.resolveId(session.tenantCode()));
        entity.setSessionId(session.sessionId());
        entity.setUserId(session.userId());
        entity.setCapabilityCode(session.capabilityCode());
        entity.setTitle(session.title());
        entity.setStatus(session.status().name());
        entity.setCreatedAt(session.createdAt());
        entity.setUpdatedAt(session.updatedAt());
        return entity;
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private ConversationSession toDomain(ConversationSessionEntity entity) {
        return new ConversationSession(
                TenantIds.toCode(entity.getTenantId()),
                entity.getSessionId(),
                entity.getUserId(),
                entity.getCapabilityCode(),
                entity.getTitle(),
                SessionStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
