package cn.cyc.ai.cog.runtime.session.service;

import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.api.ChatMessage;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;
import cn.cyc.ai.cog.runtime.session.domain.ConversationSession;
import cn.cyc.ai.cog.runtime.session.domain.MessageRole;
import cn.cyc.ai.cog.runtime.session.domain.SessionStatus;
import cn.cyc.ai.cog.runtime.session.spi.ConversationMessageRepository;
import cn.cyc.ai.cog.runtime.session.spi.ConversationSessionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runtime 会话上下文管理测试。
 *
 * @author cyc
 */
class RuntimeConversationContextManagerTest {

    private RuntimeConversationContextManager manager;
    private InMemorySessionRepository sessionRepository;
    private InMemoryMessageRepository messageRepository;

    @BeforeEach
    void setUp() {
        ConversationProperties properties = new ConversationProperties();
        properties.setMaxHistoryMessages(2);
        properties.setMaxMessageChars(7);
        sessionRepository = new InMemorySessionRepository();
        messageRepository = new InMemoryMessageRepository();
        manager = new RuntimeConversationContextManager(
                properties,
                sessionRepository,
                messageRepository
        );
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
        TenantContext.clear();
    }

    @Test
    void shouldDisableContextWhenSessionIdIsMissing() {
        ConversationContext context = manager.load(sampleContext(Map.of()));

        assertFalse(context.enabled());
        assertTrue(context.recentMessages().isEmpty());
    }

    @Test
    void shouldLoadRecentMessagesAndTrimLongContent() {
        sessionRepository.session = activeSession("capability.qa");
        messageRepository.messages.add(message("1", MessageRole.USER, "第一轮用户问题"));
        messageRepository.messages.add(message("2", MessageRole.ASSISTANT, "第一轮助手回答"));
        messageRepository.messages.add(message("3", MessageRole.USER, "第二轮很长很长的问题"));

        ConversationContext context = manager.load(sampleContext(Map.of("sessionId", "s-1")));

        assertTrue(context.enabled());
        assertEquals(2, context.recentMessages().size());
        assertEquals("第一轮助手回答", context.recentMessages().get(0).content());
        assertEquals("第二轮很长很长", context.recentMessages().get(1).content());
    }

    @Test
    void shouldRejectSessionBoundToDifferentCapability() {
        sessionRepository.session = activeSession("capability.other");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> manager.load(sampleContext(Map.of("sessionId", "s-1"))));

        assertEquals("CONFLICT", exception.getSemanticCode());
    }

    @Test
    void shouldRejectSessionOwnedByDifferentUserWhenUserContextExists() {
        UserContext.set(new AuthUser(2L, "other", "default", List.of(), List.of()));
        sessionRepository.session = activeSession("capability.qa");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> manager.load(sampleContext(Map.of("sessionId", "s-1"))));

        assertEquals("FORBIDDEN", exception.getSemanticCode());
    }

    @Test
    void shouldRejectSessionOwnedByDifferentTenant() {
        TenantContext.setTenantCode("tenant-a");
        sessionRepository.session = activeSession("tenant-b", "capability.qa", "1");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> manager.load(sampleContext(Map.of("sessionId", "s-1"))));

        assertEquals("FORBIDDEN", exception.getSemanticCode());
    }

    @Test
    void shouldLoadSessionOwnedByCurrentUser() {
        UserContext.set(new AuthUser(1L, "owner", "default", List.of(), List.of()));
        sessionRepository.session = activeSession("capability.qa", "1");

        ConversationContext context = manager.load(sampleContext(Map.of("sessionId", "s-1")));

        assertTrue(context.enabled());
    }

    @Test
    void shouldAugmentPromptInputWithConversationHistory() throws Exception {
        sessionRepository.session = activeSession("capability.qa");
        messageRepository.messages.add(message("1", MessageRole.USER, "上一轮问题"));
        messageRepository.messages.add(message("2", MessageRole.ASSISTANT, "上一轮回答"));
        ConversationContext conversationContext = manager.load(sampleContext(Map.of("sessionId", "s-1")));

        Object promptInput = manager.augmentPromptInput(
                sampleContext(Map.of("sessionId", "s-1")),
                "当前问题",
                conversationContext);

        Map<?, ?> payload = assertInstanceOf(Map.class, promptInput);
        assertEquals("当前问题", payload.get("currentInput"));
        JsonNode history = new ObjectMapper().valueToTree(payload.get("conversationHistory"));
        assertEquals("USER", history.get(0).get("role").asText());
        assertEquals("上一轮问题", history.get(0).get("content").asText());
    }

    @Test
    void shouldAppendConversationMessagesAfterExecution() {
        sessionRepository.session = activeSession("capability.qa");

        manager.recordExecution(
                sampleContext(Map.of("sessionId", "s-1")),
                "当前问题",
                "当前回答");

        assertEquals(2, messageRepository.messages.size());
        assertEquals(MessageRole.USER, messageRepository.messages.get(0).role());
        assertEquals("当前问题", messageRepository.messages.get(0).content());
        assertEquals(MessageRole.ASSISTANT, messageRepository.messages.get(1).role());
        assertEquals("当前回答", messageRepository.messages.get(1).content());
    }

    @Test
    void shouldInsertHistoryMessagesAfterSystemMessageForReAct() {
        sessionRepository.session = activeSession("capability.qa");
        messageRepository.messages.add(message("1", MessageRole.USER, "上一轮问题"));
        messageRepository.messages.add(message("2", MessageRole.ASSISTANT, "上一轮回答"));
        ConversationContext conversationContext = manager.load(sampleContext(Map.of("sessionId", "s-1")));

        List<ChatMessage> messages = manager.augmentMessages(
                List.of(ChatMessage.system("system"), ChatMessage.user("当前问题")),
                conversationContext);

        assertEquals("system", messages.get(0).role());
        assertEquals("上一轮问题", messages.get(1).content());
        assertEquals("上一轮回答", messages.get(2).content());
        assertEquals("当前问题", messages.get(3).content());
    }

    private ExecutionContext sampleContext(Map<String, Object> parameters) {
        SchemaDefinition schema = new SchemaDefinition("object", "schema", true, Map.of(), null, List.of());
        CapabilityDefinition capability = new CapabilityDefinition(
                "capability.qa",
                "问答",
                "测试",
                schema,
                schema,
                Map.of(),
                ExecutionMode.SYNC,
                "agent.qa",
                RiskLevel.LOW,
                false,
                CommonStatus.ENABLED
        );
        return new ExecutionContext(
                "trace-1",
                new CapabilityExecuteRequest("capability.qa", Map.of("question", "当前问题"), parameters),
                capability,
                null,
                null,
                List.of(),
                Map.of()
        );
    }

    private ConversationSession activeSession(String capabilityCode) {
        return activeSession(capabilityCode, "u-1");
    }

    private ConversationSession activeSession(String capabilityCode, String userId) {
        return activeSession("default", capabilityCode, userId);
    }

    private ConversationSession activeSession(String tenantCode, String capabilityCode, String userId) {
        Instant now = Instant.now();
        return new ConversationSession(tenantCode, "s-1", userId, capabilityCode, "标题",
                SessionStatus.ACTIVE, now, now);
    }

    private ConversationMessage message(String id, MessageRole role, String content) {
        return new ConversationMessage("default", id, "s-1", role, content, "trace-old", Instant.now());
    }

    private static class InMemorySessionRepository implements ConversationSessionRepository {

        private ConversationSession session;

        @Override
        public Optional<ConversationSession> findBySessionId(String sessionId) {
            return Optional.ofNullable(session);
        }

        @Override
        public void save(ConversationSession session) {
            this.session = session;
        }
    }

    private static class InMemoryMessageRepository implements ConversationMessageRepository {

        private final List<ConversationMessage> messages = new ArrayList<>();

        @Override
        public void save(ConversationMessage message) {
            messages.add(message);
        }

        @Override
        public List<ConversationMessage> findBySessionId(String sessionId) {
            return List.copyOf(messages);
        }
    }
}
