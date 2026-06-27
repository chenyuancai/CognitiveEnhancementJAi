package cn.cyc.ai.cog.platform.operations.dto;

public record MessageSendRequest(
        String channel,
        String recipient,
        String templateCode,
        String renderedContent
) {
}
