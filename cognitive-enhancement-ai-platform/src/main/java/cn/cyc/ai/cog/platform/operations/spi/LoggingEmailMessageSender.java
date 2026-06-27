package cn.cyc.ai.cog.platform.operations.spi;

import cn.cyc.ai.cog.platform.operations.dto.MessageSendRequest;
import cn.cyc.ai.cog.platform.operations.dto.MessageSendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 邮件通道：记录结构化日志；生产可替换为 JavaMail / 第三方 SDK 实现。
 */
@Component
@Order(10)
public class LoggingEmailMessageSender implements MessageSender {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailMessageSender.class);

    @Override
    public boolean supports(String channel) {
        return "EMAIL".equalsIgnoreCase(channel);
    }

    @Override
    public MessageSendResult send(MessageSendRequest request) {
        String messageId = "email-" + UUID.randomUUID();
        log.info("邮件触达，to={}, templateCode={}, messageId={}, contentLength={}",
                request.recipient(), request.templateCode(), messageId,
                request.renderedContent() == null ? 0 : request.renderedContent().length());
        return new MessageSendResult(true, "EMAIL", messageId, "logged");
    }
}
