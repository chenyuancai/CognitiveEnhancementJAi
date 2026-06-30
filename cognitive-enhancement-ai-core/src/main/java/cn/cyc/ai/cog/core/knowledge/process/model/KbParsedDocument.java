package cn.cyc.ai.cog.core.knowledge.process.model;

import java.util.List;

/**
 * 解析后的知识文档。
 */
public record KbParsedDocument(
        String title,
        String markdown,
        String plainText,
        List<KbImageRef> images
) {
}
