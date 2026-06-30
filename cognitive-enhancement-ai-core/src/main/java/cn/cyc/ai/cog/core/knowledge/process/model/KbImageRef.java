package cn.cyc.ai.cog.core.knowledge.process.model;

/**
 * Markdown 中的图片引用。
 */
public record KbImageRef(
        String alt,
        String sourceUrl,
        String enrichedDescription
) {
}
