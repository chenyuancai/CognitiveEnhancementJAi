package cn.cyc.ai.cog.platform.operations.dto;

/**
 * 消息Send请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record MessageSendRequest(
        String channel,
        String recipient,
        String templateCode,
        String renderedContent
) {
}
