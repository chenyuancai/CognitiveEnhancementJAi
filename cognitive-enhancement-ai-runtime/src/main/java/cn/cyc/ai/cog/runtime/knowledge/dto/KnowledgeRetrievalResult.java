package cn.cyc.ai.cog.runtime.knowledge.dto;

import cn.cyc.ai.cog.runtime.knowledge.domain.KnowledgeFragment;

import java.util.List;

/**
 * 知识检索结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record KnowledgeRetrievalResult(
        String query,
        String scenarioCode,
        int total,
        List<KnowledgeFragment> items
) {
}
