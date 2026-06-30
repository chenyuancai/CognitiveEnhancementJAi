package cn.cyc.ai.cog.platform.operations.spi;

import cn.cyc.ai.cog.platform.operations.dto.MessageSendRequest;
import cn.cyc.ai.cog.platform.operations.dto.MessageSendResult;

/**
 * 消息触达 SPI：SMS / EMAIL / IN_APP 等通道由实现类对接第三方。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface MessageSender {

    /** 是否支持该渠道（如 SMS、EMAIL、IN_APP）。 */
    boolean supports(String channel);

    /** 发送已渲染内容；默认实现可仅记录日志。 */
    MessageSendResult send(MessageSendRequest request);
}
