package cn.cyc.ai.cog.runtime.session.service;

import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;
import cn.cyc.ai.cog.runtime.session.domain.ConversationSession;
import cn.cyc.ai.cog.runtime.session.domain.MessageRole;
import cn.cyc.ai.cog.runtime.session.domain.SessionStatus;
import cn.cyc.ai.cog.runtime.session.spi.ConversationMessageRepository;
import cn.cyc.ai.cog.runtime.session.spi.ConversationSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 会话服务。
 *
 * @author cyc
 */
@Service
public class ConversationSessionService {

    /**
     * 会话仓储。
     */
    private final ConversationSessionRepository conversationSessionRepository;

    /**
     * 会话消息仓储。
     */
    private final ConversationMessageRepository conversationMessageRepository;

    /**
     * 构造会话服务。
     *
     * @param conversationSessionRepository 会话仓储
     * @param conversationMessageRepository 会话消息仓储
     */
    public ConversationSessionService(ConversationSessionRepository conversationSessionRepository,
                                      ConversationMessageRepository conversationMessageRepository) {
        this.conversationSessionRepository = conversationSessionRepository;
        this.conversationMessageRepository = conversationMessageRepository;
    }

    /**
     * 创建会话。
     *
     * @param userId         用户 ID
     * @param capabilityCode 能力编码
     * @param title          会话标题
     * @return 新建会话
     */
    public ConversationSession createSession(String userId, String capabilityCode, String title) {
        Instant now = Instant.now();
        String ownerUserId = resolveOwnerUserId(userId);
        ConversationSession session = new ConversationSession(
                TenantContext.currentTenantCode(),
                UUID.randomUUID().toString(),
                ownerUserId,
                capabilityCode,
                title,
                SessionStatus.ACTIVE,
                now,
                now
        );
        conversationSessionRepository.save(session);
        return session;
    }

    /**
     * 查询会话详情。
     *
     * @param sessionId 会话 ID
     * @return 会话记录
     */
    public ConversationSession getSession(String sessionId) {
        ConversationSession session = conversationSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到会话: " + sessionId));
        validateOwner(session);
        return session;
    }

    /**
     * 查询会话消息列表。
     *
     * @param sessionId 会话 ID
     * @return 消息列表
     */
    public List<ConversationMessage> listMessages(String sessionId) {
        getSession(sessionId);
        return conversationMessageRepository.findBySessionId(sessionId);
    }

    /**
     * 追加会话消息。
     *
     * @param sessionId 会话 ID
     * @param role      消息角色
     * @param content   消息内容
     * @param traceId   关联 TraceId
     * @return 新增消息
     */
    public ConversationMessage appendMessage(String sessionId, MessageRole role, String content, String traceId) {
        ConversationSession session = getSession(sessionId);
        Instant now = Instant.now();
        ConversationMessage message = new ConversationMessage(
                session.tenantCode(),
                UUID.randomUUID().toString(),
                sessionId,
                role,
                content,
                traceId,
                now
        );
        conversationMessageRepository.save(message);
        conversationSessionRepository.save(new ConversationSession(
                session.tenantCode(),
                session.sessionId(),
                session.userId(),
                session.capabilityCode(),
                session.title(),
                session.status(),
                session.createdAt(),
                now
        ));
        return message;
    }

    /**
     * 记录一次能力执行到会话消息。
     *
     * @param sessionId 会话 ID
     * @param request   能力执行请求
     * @param response  能力执行响应
     */
    public void recordExecution(String sessionId,
                                CapabilityExecuteRequest request,
                                CapabilityExecuteResponse response) {
        String traceId = response == null ? null : response.traceId();
        appendMessage(sessionId, MessageRole.USER, extractUserContent(request), traceId);
        appendMessage(sessionId, MessageRole.ASSISTANT, extractAssistantContent(response), traceId);
    }

    private String extractUserContent(CapabilityExecuteRequest request) {
        if (request == null || request.input() == null || request.input().isEmpty()) {
            return "";
        }
        Object question = request.input().get("question");
        return question == null ? request.input().toString() : question.toString();
    }

    private String extractAssistantContent(CapabilityExecuteResponse response) {
        if (response == null || response.result() == null) {
            return "";
        }
        ExecutionResult result = response.result();
        if (StringUtils.hasText(result.message())) {
            return result.message();
        }
        Object businessOutput = result.output().get("businessOutput");
        return businessOutput == null ? result.output().toString() : businessOutput.toString();
    }

    private String resolveOwnerUserId(String requestedUserId) {
        Long currentUserId = UserContext.currentUserId();
        if (currentUserId == null) {
            return requestedUserId;
        }
        String current = String.valueOf(currentUserId);
        if (StringUtils.hasText(requestedUserId) && !current.equals(requestedUserId)) {
            throw new BusinessException("FORBIDDEN", "不能为其他用户创建会话");
        }
        return current;
    }

    private void validateOwner(ConversationSession session) {
        Long currentUserId = UserContext.currentUserId();
        if (currentUserId != null
                && StringUtils.hasText(session.userId())
                && !String.valueOf(currentUserId).equals(session.userId())) {
            throw new BusinessException("FORBIDDEN", "会话不属于当前用户: " + session.sessionId());
        }
    }
}
