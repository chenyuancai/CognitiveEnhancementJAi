package cn.cyc.ai.cog.runtime.session.service;

import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;
import cn.cyc.ai.cog.runtime.session.domain.ConversationSession;
import cn.cyc.ai.cog.runtime.session.domain.MessageRole;
import cn.cyc.ai.cog.runtime.session.domain.SessionStatus;
import cn.cyc.ai.cog.runtime.session.dto.ConversationSessionPageQuery;
import cn.cyc.ai.cog.runtime.session.spi.ConversationMessageRepository;
import cn.cyc.ai.cog.runtime.session.spi.ConversationSessionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 会话服务测试。
 *
 * @author cyc
 */
class ConversationSessionServiceTest {

    private ConversationSessionService service;
    private InMemorySessionRepository sessionRepository;
    private InMemoryMessageRepository messageRepository;

    @BeforeEach
    void setUp() {
        sessionRepository = new InMemorySessionRepository();
        messageRepository = new InMemoryMessageRepository();
        service = new ConversationSessionService(sessionRepository, messageRepository);
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void shouldUseCurrentUserWhenCreatingSessionWithoutUserId() {
        UserContext.set(new AuthUser(1L, "owner", "default", List.of(), List.of()));

        ConversationSession session = service.createSession(null, "capability.qa", "标题");

        assertEquals("1", session.userId());
    }

    @Test
    void shouldRejectCreatingSessionForDifferentUser() {
        UserContext.set(new AuthUser(1L, "owner", "default", List.of(), List.of()));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.createSession("2", "capability.qa", "标题"));

        assertEquals("FORBIDDEN", exception.getSemanticCode());
    }

    @Test
    void shouldRejectReadingSessionOwnedByDifferentUser() {
        UserContext.set(new AuthUser(2L, "other", "default", List.of(), List.of()));
        sessionRepository.save(activeSession("1"));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.listMessages("s-1"));

        assertEquals("FORBIDDEN", exception.getSemanticCode());
    }

    @Test
    void shouldAppendMessageForCurrentUserSession() {
        UserContext.set(new AuthUser(1L, "owner", "default", List.of(), List.of()));
        sessionRepository.save(activeSession("1"));

        service.appendMessage("s-1", MessageRole.USER, "问题", "trace-1");

        assertEquals(1, messageRepository.messages.size());
        assertEquals("问题", messageRepository.messages.get(0).content());
    }

    @Test
    void shouldPageSessionsByUserAndCapability() {
        UserContext.set(new AuthUser(1L, "owner", "default", List.of(), List.of()));
        sessionRepository.save(activeSession("1", "s-1", "capability.chat.tutoring"));
        sessionRepository.save(activeSession("1", "s-2", "capability.chat.tutoring"));
        sessionRepository.save(activeSession("1", "s-3", "capability.qa"));

        PageResult<ConversationSession> page = service.pageMySessions(1, 10, "capability.chat.tutoring");

        assertEquals(2, page.getTotal());
        assertEquals(2, page.getRecords().size());
    }

    private ConversationSession activeSession(String userId, String sessionId, String capabilityCode) {
        Instant now = Instant.now();
        return new ConversationSession("default", sessionId, userId, capabilityCode, "标题",
                SessionStatus.ACTIVE, now, now);
    }

    private ConversationSession activeSession(String userId) {
        return activeSession(userId, "s-1", "capability.qa");
    }

    private static class InMemorySessionRepository implements ConversationSessionRepository {

        private final java.util.Map<String, ConversationSession> sessions = new java.util.concurrent.ConcurrentHashMap<>();

        @Override
        public Optional<ConversationSession> findBySessionId(String sessionId) {
            return Optional.ofNullable(sessions.get(sessionId));
        }

        @Override
        public void save(ConversationSession session) {
            sessions.put(session.sessionId(), session);
        }

        @Override
        public List<ConversationSession> listByUserAndCapability(String userId, String capabilityCode) {
            return sessions.values().stream()
                    .filter(session -> userId.equals(session.userId()))
                    .filter(session -> capabilityCode == null || capabilityCode.equals(session.capabilityCode()))
                    .sorted(java.util.Comparator.comparing(ConversationSession::updatedAt).reversed())
                    .toList();
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

        @Override
        public java.util.Optional<ConversationMessage> findLatestBySessionId(String sessionId) {
            return messages.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(messages.get(messages.size() - 1));
        }
    }
}
