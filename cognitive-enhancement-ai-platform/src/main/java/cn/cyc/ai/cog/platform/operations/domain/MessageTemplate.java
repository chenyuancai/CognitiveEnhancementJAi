package cn.cyc.ai.cog.platform.operations.domain;

public record MessageTemplate(
        Long id,
        String templateCode,
        String templateName,
        String channel,
        String content,
        String variableSchema,
        String status
) {
}
