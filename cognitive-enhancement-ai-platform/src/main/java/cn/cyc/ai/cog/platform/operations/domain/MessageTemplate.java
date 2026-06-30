package cn.cyc.ai.cog.platform.operations.domain;

/**
 * MessageTemplate 记录
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
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
