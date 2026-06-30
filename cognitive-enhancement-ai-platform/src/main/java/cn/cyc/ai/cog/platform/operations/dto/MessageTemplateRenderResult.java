package cn.cyc.ai.cog.platform.operations.dto;

/**
 * 消息TemplateRender结果
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record MessageTemplateRenderResult(
        String templateCode,
        String channel,
        String renderedContent
) {
}
