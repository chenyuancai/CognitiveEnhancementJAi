package cn.cyc.ai.cog.platform.knowledge.domain;

public record KnowledgePackage(
        Long id,
        String packageName,
        String description,
        String status
) {
}
