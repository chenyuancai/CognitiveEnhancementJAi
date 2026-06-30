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
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
@Order(100)
public class DefaultNoopMessageSender implements MessageSender {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(DefaultNoopMessageSender.class);

    /**
     * 执行supports。
     *
     * @param channel channel
     * @return 执行结果
     */
    @Override
    public boolean supports(String channel) {
        return true;
    }

    /**
     * 执行send。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Override
    public MessageSendResult send(MessageSendRequest request) {
        String messageId = "noop-" + UUID.randomUUID();
        log.info("消息触达占位发送，channel={}, recipient={}, templateCode={}, messageId={}",
                request.channel(), request.recipient(), request.templateCode(), messageId);
        return new MessageSendResult(true, request.channel(), messageId, "noop");
    }
}
