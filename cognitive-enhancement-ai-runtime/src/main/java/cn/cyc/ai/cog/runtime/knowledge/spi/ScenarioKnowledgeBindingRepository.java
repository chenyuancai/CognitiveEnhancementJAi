package cn.cyc.ai.cog.runtime.knowledge.spi;

import cn.cyc.ai.cog.runtime.knowledge.domain.ScenarioKnowledgeBinding;

import java.util.List;

/**
 * 场景知识绑定仓储接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface ScenarioKnowledgeBindingRepository {

    /**
     * 保存场景知识绑定。
     *
     * @param binding 场景知识绑定
     */
    void save(ScenarioKnowledgeBinding binding);

    /**
     * 按场景编码查询当前租户绑定列表。
     *
     * @param scenarioCode 场景编码
     * @return 绑定列表
     */
    List<ScenarioKnowledgeBinding> findByScenarioCode(String scenarioCode);
}
