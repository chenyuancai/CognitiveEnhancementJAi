package cn.cyc.ai.cog.platform.operations.spi;

import cn.cyc.ai.cog.platform.operations.dto.MessageSendRequest;
import cn.cyc.ai.cog.platform.operations.dto.MessageSendResult;
import cn.cyc.ai.cog.platform.operations.repository.InAppMessageRepository;
import cn.cyc.ai.cog.platform.operations.domain.InAppMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageSenderDispatchTest {

    @Mock
    private InAppMessageRepository inAppMessageRepository;

    @Test
    void shouldRouteInAppChannelToRepository() {
        InAppMessageSender sender = new InAppMessageSender(inAppMessageRepository);
        when(inAppMessageRepository.save(any(), eq(9L), eq("welcome"), eq("welcome"), eq("hello")))
                .thenReturn(new InAppMessage(100L, 1L, 9L, "welcome", "welcome", "hello", false, LocalDateTime.now()));

        MessageSendResult result = sender.send(new MessageSendRequest("IN_APP", "9", "welcome", "hello"));
        assertTrue(result.accepted());
        assertEquals("IN_APP", result.channel());
        assertEquals("100", result.messageId());
        verify(inAppMessageRepository).save(any(), eq(9L), eq("welcome"), eq("welcome"), eq("hello"));
    }

    @Test
    void shouldSupportDedicatedChannelsBeforeNoop() {
        LoggingEmailMessageSender email = new LoggingEmailMessageSender();
        LoggingSmsMessageSender sms = new LoggingSmsMessageSender();
        DefaultNoopMessageSender noop = new DefaultNoopMessageSender();
        List<MessageSender> senders = List.of(email, sms, noop);

        assertTrue(senders.stream().anyMatch(s -> s.supports("EMAIL")));
        MessageSendResult emailResult = email.send(new MessageSendRequest("EMAIL", "a@b.c", "t1", "body"));
        assertEquals("EMAIL", emailResult.channel());
    }
}
