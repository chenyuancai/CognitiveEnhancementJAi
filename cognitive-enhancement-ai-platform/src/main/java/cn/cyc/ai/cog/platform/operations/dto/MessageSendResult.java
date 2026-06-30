package cn.cyc.ai.cog.platform.operations.dto;

/**
 * 消息Send结果
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record MessageSendResult(
        boolean accepted,
        String channel,
        String messageId,
        String detail
) {
}
