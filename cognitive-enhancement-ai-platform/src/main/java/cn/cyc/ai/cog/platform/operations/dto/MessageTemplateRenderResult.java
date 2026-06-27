package cn.cyc.ai.cog.platform.operations.dto;

public record MessageTemplateRenderResult(
        String templateCode,
        String channel,
        String renderedContent
) {
}
