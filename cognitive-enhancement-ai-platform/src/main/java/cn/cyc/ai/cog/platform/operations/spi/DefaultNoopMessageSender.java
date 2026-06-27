package cn.cyc.ai.cog.platform.operations.spi;

import cn.cyc.ai.cog.platform.operations.dto.MessageSendRequest;
import cn.cyc.ai.cog.platform.operations.dto.MessageSendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 默认消息发送占位：记录日志并返回 accepted，不调用外部通道。
 */
@Component
@Order(100)
public class DefaultNoopMessageSender implements MessageSender {

    private static final Logger log = LoggerFactory.getLogger(DefaultNoopMessageSender.class);

    @Override
    public boolean supports(String channel) {
        return true;
    }

    @Override
    public MessageSendResult send(MessageSendRequest request) {
        String messageId = "noop-" + UUID.randomUUID();
        log.info("消息触达占位发送，channel={}, recipient={}, templateCode={}, messageId={}",
                request.channel(), request.recipient(), request.templateCode(), messageId);
        return new MessageSendResult(true, request.channel(), messageId, "noop");
    }
}
