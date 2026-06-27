package cn.cyc.ai.cog.platform.operations.dto;

public record MessageSendResult(
        boolean accepted,
        String channel,
        String messageId,
        String detail
) {
}
