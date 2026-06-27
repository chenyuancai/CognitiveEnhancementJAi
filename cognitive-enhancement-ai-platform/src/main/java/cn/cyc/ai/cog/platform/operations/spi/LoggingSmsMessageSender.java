package cn.cyc.ai.cog.platform.operations.spi;

import cn.cyc.ai.cog.platform.operations.dto.MessageSendRequest;
import cn.cyc.ai.cog.platform.operations.dto.MessageSendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 短信通道：记录结构化日志；生产可替换为运营商 / 云短信 SDK。
 */
@Component
@Order(10)
public class LoggingSmsMessageSender implements MessageSender {

    private static final Logger log = LoggerFactory.getLogger(LoggingSmsMessageSender.class);

    @Override
    public boolean supports(String channel) {
        return "SMS".equalsIgnoreCase(channel);
    }

    @Override
    public MessageSendResult send(MessageSendRequest request) {
        String messageId = "sms-" + UUID.randomUUID();
        log.info("短信触达，mobile={}, templateCode={}, messageId={}, contentLength={}",
                request.recipient(), request.templateCode(), messageId,
                request.renderedContent() == null ? 0 : request.renderedContent().length());
        return new MessageSendResult(true, "SMS", messageId, "logged");
    }
}
