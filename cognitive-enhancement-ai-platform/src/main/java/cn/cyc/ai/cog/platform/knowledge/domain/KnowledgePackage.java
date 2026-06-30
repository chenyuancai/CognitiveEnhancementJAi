package cn.cyc.ai.cog.platform.knowledge.domain;

/**
 * KnowledgePackage 记录
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record KnowledgePackage(
        Long id,
        String packageName,
        String description,
        String status
) {
}
