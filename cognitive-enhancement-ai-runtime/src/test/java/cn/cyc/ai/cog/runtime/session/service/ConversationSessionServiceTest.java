package cn.cyc.ai.cog.runtime.session.service;

import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;
import cn.cyc.ai.cog.runtime.session.domain.ConversationSession;
import cn.cyc.ai.cog.runtime.session.domain.MessageRole;
import cn.cyc.ai.cog.runtime.session.domain.SessionStatus;
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
        sessionRepository.session = activeSession("1");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.listMessages("s-1"));

        assertEquals("FORBIDDEN", exception.getSemanticCode());
    }

    @Test
    void shouldAppendMessageForCurrentUserSession() {
        UserContext.set(new AuthUser(1L, "owner", "default", List.of(), List.of()));
        sessionRepository.session = activeSession("1");

        service.appendMessage("s-1", MessageRole.USER, "问题", "trace-1");

        assertEquals(1, messageRepository.messages.size());
        assertEquals("问题", messageRepository.messages.get(0).content());
    }

    private ConversationSession activeSession(String userId) {
        Instant now = Instant.now();
        return new ConversationSession("default", "s-1", userId, "capability.qa", "标题",
                SessionStatus.ACTIVE, now, now);
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
