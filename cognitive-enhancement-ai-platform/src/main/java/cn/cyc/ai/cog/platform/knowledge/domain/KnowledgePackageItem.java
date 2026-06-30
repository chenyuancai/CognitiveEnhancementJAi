package cn.cyc.ai.cog.platform.knowledge.domain;

/**
 * KnowledgePackageItem 记录
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record KnowledgePackageItem(
        Long id,
        Long packageId,
        Long parentId,
        Long contentId,
        String title,
        Integer sortNo
) {
}
