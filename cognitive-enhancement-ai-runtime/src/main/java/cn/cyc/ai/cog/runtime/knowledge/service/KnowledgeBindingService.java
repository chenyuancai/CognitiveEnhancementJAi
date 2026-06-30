package cn.cyc.ai.cog.runtime.knowledge.service;

import cn.cyc.ai.cog.runtime.knowledge.domain.ScenarioKnowledgeBinding;
import cn.cyc.ai.cog.runtime.knowledge.spi.ScenarioKnowledgeBindingRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 场景知识绑定服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class KnowledgeBindingService {

    /**
     * 场景知识绑定仓储。
     */
    private final ScenarioKnowledgeBindingRepository scenarioKnowledgeBindingRepository;

    /**
     * 构造场景知识绑定服务。
     *
     * @param scenarioKnowledgeBindingRepository 场景知识绑定仓储
     */
    public KnowledgeBindingService(ScenarioKnowledgeBindingRepository scenarioKnowledgeBindingRepository) {
        this.scenarioKnowledgeBindingRepository = scenarioKnowledgeBindingRepository;
    }

    /**
     * 绑定场景与知识库。
     *
     * @param scenarioCode  场景编码
     * @param knowledgeCode 知识库编码
     * @param priority      优先级
     * @param enabled       是否启用
     * @return 新建绑定记录
     */
    public ScenarioKnowledgeBinding bindScenario(String scenarioCode,
                                                 String knowledgeCode,
                                                 int priority,
                                                 boolean enabled) {
        ScenarioKnowledgeBinding binding = new ScenarioKnowledgeBinding(
                TenantContext.currentTenantCode(),
                UUID.randomUUID().toString(),
                scenarioCode,
                knowledgeCode,
                priority,
                enabled,
                Instant.now()
        );
        scenarioKnowledgeBindingRepository.save(binding);
        return binding;
    }

    /**
     * 查询场景绑定列表。
     *
     * @param scenarioCode 场景编码
     * @return 绑定列表
     */
    public List<ScenarioKnowledgeBinding> listBindings(String scenarioCode) {
        return scenarioKnowledgeBindingRepository.findByScenarioCode(scenarioCode);
    }
}
