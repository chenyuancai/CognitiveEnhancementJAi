package cn.cyc.ai.cog.runtime.session.service;

import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.api.ChatMessage;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Runtime 会话上下文管理器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class RuntimeConversationContextManager {

    /** properties。 */
    private final ConversationProperties properties;
    /** 会话仓储。 */
    private final ConversationSessionRepository sessionRepository;
    /** 消息仓储。 */
    private final ConversationMessageRepository messageRepository;

    /**
     * 创建RuntimeConversationContextManager。
     */
    public RuntimeConversationContextManager(ConversationProperties properties,
                                             ConversationSessionRepository sessionRepository,
                                             ConversationMessageRepository messageRepository) {
        this.properties = properties;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * 根据请求参数加载会话上下文。
     */
    public ConversationContext load(ExecutionContext context) {
        String sessionId = sessionId(context);
        if (!properties.isEnabled() || !StringUtils.hasText(sessionId)) {
            return ConversationContext.disabled();
        }
        ConversationSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到会话: " + sessionId));
        validateSession(context, session);
        List<ConversationMessage> messages = messageRepository.findBySessionId(sessionId);
        List<ConversationMessage> recent = takeRecent(messages, Math.max(0, properties.getMaxHistoryMessages()))
                .stream()
                .map(this::trimMessage)
                .toList();
        return new ConversationContext(sessionId, recent, true);
    }

    /**
     * 为普通 LLM promptInput 注入结构化历史。
     */
    public Object augmentPromptInput(ExecutionContext context, Object promptInput, ConversationContext conversationContext) {
        if (conversationContext == null || !conversationContext.enabled() || conversationContext.recentMessages().isEmpty()) {
            return promptInput;
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("conversationHistory", toHistoryPayload(conversationContext.recentMessages()));
        payload.put("currentInput", promptInput);
        payload.put("sessionId", conversationContext.sessionId());
        return payload;
    }

    /**
     * 为 ReAct messages 注入历史消息。system 消息保留在首位，当前 user 消息保留在最后。
     */
    public List<ChatMessage> augmentMessages(List<ChatMessage> baseMessages, ConversationContext conversationContext) {
        if (conversationContext == null || !conversationContext.enabled() || conversationContext.recentMessages().isEmpty()) {
            return baseMessages;
        }
        List<ChatMessage> messages = new ArrayList<>();
        if (!baseMessages.isEmpty() && "system".equals(baseMessages.get(0).role())) {
            messages.add(baseMessages.get(0));
            appendHistory(messages, conversationContext.recentMessages());
            messages.addAll(baseMessages.subList(1, baseMessages.size()));
            return messages;
        }
        appendHistory(messages, conversationContext.recentMessages());
        messages.addAll(baseMessages);
        return messages;
    }

    /**
     * 记录一次成功执行的用户问题与助手回答。
     */
    public void recordExecution(ExecutionContext context, String userContent, String assistantContent) {
        String sessionId = sessionId(context);
        if (!properties.isEnabled() || !StringUtils.hasText(sessionId)) {
            return;
        }
        ConversationSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到会话: " + sessionId));
        validateSession(context, session);
        append(session, MessageRole.USER, userContent, context.traceId());
        append(session, MessageRole.ASSISTANT, assistantContent, context.traceId());
        sessionRepository.save(new ConversationSession(
                session.tenantCode(),
                session.sessionId(),
                session.userId(),
                session.capabilityCode(),
                session.title(),
                session.status(),
                session.createdAt(),
                Instant.now()
        ));
    }

    /**
     * 执行会话 ID。
     *
     * @param context 上下文
     * @return 执行结果
     */
    private String sessionId(ExecutionContext context) {
        Object value = context.request().parameters().get("sessionId");
        return value == null ? null : value.toString();
    }

    /**
     * 校验参数。
     *
     * @param context 上下文
     * @param session 会话
     */
    private void validateSession(ExecutionContext context, ConversationSession session) {
        if (session.status() != SessionStatus.ACTIVE) {
            throw new BusinessException("CONFLICT", "会话未激活: " + session.sessionId());
        }
        if (!TenantContext.currentTenantCode().equals(TenantContext.normalize(session.tenantCode()))) {
            throw new BusinessException("FORBIDDEN", "会话不属于当前租户: " + session.sessionId());
        }
        if (!context.capability().capabilityCode().equals(session.capabilityCode())) {
            throw new BusinessException("CONFLICT", "会话绑定能力与当前请求不一致: " + session.sessionId());
        }
        Long currentUserId = UserContext.currentUserId();
        if (currentUserId != null
                && StringUtils.hasText(session.userId())
                && !String.valueOf(currentUserId).equals(session.userId())) {
            throw new BusinessException("FORBIDDEN", "会话不属于当前用户: " + session.sessionId());
        }
    }

    /**
     * 执行takeRecent。
     *
     * @param messages messages
     * @param limit 限制
     * @return 执行结果
     */
    private List<ConversationMessage> takeRecent(List<ConversationMessage> messages, int limit) {
        if (limit <= 0 || messages.size() <= limit) {
            return messages;
        }
        return messages.subList(messages.size() - limit, messages.size());
    }

    /**
     * 执行trim消息。
     *
     * @param message 消息
     * @return 执行结果
     */
    private ConversationMessage trimMessage(ConversationMessage message) {
        String content = message.content();
        int maxChars = Math.max(0, properties.getMaxMessageChars());
        if (maxChars > 0 && content != null && content.length() > maxChars) {
            content = content.substring(0, maxChars);
        }
        return new ConversationMessage(
                message.tenantCode(),
                message.messageId(),
                message.sessionId(),
                message.role(),
                content,
                message.traceId(),
                message.recordedAt()
        );
    }

    private List<Map<String, Object>> toHistoryPayload(List<ConversationMessage> messages) {
        return messages.stream()
                .map(message -> Map.<String, Object>of(
                        "role", message.role().name(),
                        "content", message.content() == null ? "" : message.content()
                ))
                .toList();
    }

    /**
     * 执行appendHistory。
     *
     * @param target 目标
     * @param history history
     */
    private void appendHistory(List<ChatMessage> target, List<ConversationMessage> history) {
        for (ConversationMessage message : history) {
            if (message.role() == MessageRole.USER) {
                target.add(ChatMessage.user(message.content()));
            } else if (message.role() == MessageRole.ASSISTANT) {
                target.add(ChatMessage.assistant(message.content(), List.of()));
            }
        }
    }

    /**
     * 执行append。
     *
     * @param session 会话
     * @param role 角色
     * @param content 内容
     * @param traceId 链路 Trace ID
     */
    private void append(ConversationSession session, MessageRole role, String content, String traceId) {
        messageRepository.save(new ConversationMessage(
                session.tenantCode(),
                UUID.randomUUID().toString(),
                session.sessionId(),
                role,
                content == null ? "" : content,
                traceId,
                Instant.now()
        ));
    }

}
