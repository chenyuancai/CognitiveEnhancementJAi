package cn.cyc.ai.cog.runtime.knowledge.repository;

import cn.cyc.ai.cog.runtime.knowledge.domain.ScenarioKnowledgeBinding;
import cn.cyc.ai.cog.runtime.knowledge.spi.ScenarioKnowledgeBindingRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 内存场景知识绑定仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryScenarioKnowledgeBindingRepository implements ScenarioKnowledgeBindingRepository {

    /**
     * 内存绑定容器。
     */
    private final CopyOnWriteArrayList<ScenarioKnowledgeBinding> bindings = new CopyOnWriteArrayList<>();

    /**
     * 执行save。
     *
     * @param binding binding
     */
    @Override
    public void save(ScenarioKnowledgeBinding binding) {
        bindings.add(binding);
    }

    /**
     * 查找人Scenario编码。
     *
     * @param scenarioCode scenario编码
     * @return 查找结果
     */
    @Override
    public List<ScenarioKnowledgeBinding> findByScenarioCode(String scenarioCode) {
        String tenantCode = TenantContext.currentTenantCode();
        return bindings.stream()
                .filter(binding -> tenantCode.equals(binding.tenantCode()))
                .filter(binding -> scenarioCode.equals(binding.scenarioCode()))
                .sorted(Comparator.comparing(ScenarioKnowledgeBinding::priority)
                        .thenComparing(ScenarioKnowledgeBinding::recordedAt))
                .collect(Collectors.toList());
    }
}
