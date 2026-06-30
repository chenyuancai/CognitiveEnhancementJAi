package cn.cyc.ai.cog.platform.operations.spi;

import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.platform.operations.dto.MessageSendRequest;
import cn.cyc.ai.cog.platform.operations.dto.MessageSendResult;
import cn.cyc.ai.cog.platform.operations.repository.InAppMessageRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 站内信通道：落库 qz_ops_in_app_message。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
@Order(10)
public class InAppMessageSender implements MessageSender {

    /** inC端消息仓储。 */
    private final InAppMessageRepository inAppMessageRepository;

    /**
     * 创建InAppMessageSender。
     *
     * @param inAppMessageRepository inC端消息仓储
     */
    public InAppMessageSender(InAppMessageRepository inAppMessageRepository) {
        this.inAppMessageRepository = inAppMessageRepository;
    }

    /**
     * 执行supports。
     *
     * @param channel channel
     * @return 执行结果
     */
    @Override
    public boolean supports(String channel) {
        return "IN_APP".equalsIgnoreCase(channel);
    }

    /**
     * 执行send。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Override
    public MessageSendResult send(MessageSendRequest request) {
        Long userId = parseUserId(request.recipient());
        var saved = inAppMessageRepository.save(
                TenantContext.currentTenantId(),
                userId,
                request.templateCode(),
                request.templateCode(),
                request.renderedContent());
        return new MessageSendResult(true, "IN_APP", String.valueOf(saved.id()), "stored");
    }

    /**
     * 执行parse用户ID。
     *
     * @param recipient recipient
     * @return 执行结果
     */
    private Long parseUserId(String recipient) {
        if (!StringUtils.hasText(recipient)) {
            throw new IllegalArgumentException("站内信收件人不能为空");
        }
        try {
            return Long.parseLong(recipient.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("站内信收件人须为用户 ID：" + recipient);
        }
    }
}
