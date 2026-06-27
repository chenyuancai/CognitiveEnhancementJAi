package cn.cyc.ai.cog.platform.knowledge.domain;

public record KnowledgePackageItem(
        Long id,
        Long packageId,
        Long parentId,
        Long contentId,
        String title,
        Integer sortNo
) {
}
