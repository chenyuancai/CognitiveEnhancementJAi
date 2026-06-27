package cn.cyc.ai.cog.runtime.knowledge.dto;

import cn.cyc.ai.cog.runtime.knowledge.domain.KnowledgeFragment;

import java.util.List;

/**
 * 知识检索结果。
 *
 * @param query        检索关键词
 * @param scenarioCode 场景编码
 * @param total        命中总量
 * @param items        知识片段列表
 * @author cyc
 */
public record KnowledgeRetrievalResult(
        String query,
        String scenarioCode,
        int total,
        List<KnowledgeFragment> items
) {
}
