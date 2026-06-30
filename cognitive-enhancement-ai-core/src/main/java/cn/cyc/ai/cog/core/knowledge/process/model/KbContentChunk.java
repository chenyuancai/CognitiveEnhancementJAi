package cn.cyc.ai.cog.core.knowledge.process.model;

import java.util.List;

/**
 * 知识内容分块。
 */
public record KbContentChunk(
        int chunkIndex,
        String chunkText,
        String headingPath,
        List<Float> embedding
) {
}
