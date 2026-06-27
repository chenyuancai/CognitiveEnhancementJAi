package cn.cyc.ai.cog.runtime.knowledge.repository;

import cn.cyc.ai.cog.runtime.knowledge.domain.ScenarioKnowledgeBinding;
import cn.cyc.ai.cog.runtime.knowledge.entity.ScenarioKnowledgeBindingEntity;
import cn.cyc.ai.cog.runtime.knowledge.mapper.ScenarioKnowledgeBindingMapper;
import cn.cyc.ai.cog.runtime.knowledge.spi.ScenarioKnowledgeBindingRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.security.TenantIds;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 持久化场景知识绑定仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentScenarioKnowledgeBindingRepository implements ScenarioKnowledgeBindingRepository {

    /**
     * 仓储日志。
     */
    private static final Logger log = LoggerFactory.getLogger(PersistentScenarioKnowledgeBindingRepository.class);

    /**
     * 场景知识绑定 Mapper。
     */
    private final ScenarioKnowledgeBindingMapper scenarioKnowledgeBindingMapper;

    /**
     * 构造持久化场景知识绑定仓储。
     *
     * @param scenarioKnowledgeBindingMapper 场景知识绑定 Mapper
     */
    public PersistentScenarioKnowledgeBindingRepository(ScenarioKnowledgeBindingMapper scenarioKnowledgeBindingMapper) {
        this.scenarioKnowledgeBindingMapper = scenarioKnowledgeBindingMapper;
    }

    @Override
    public void save(ScenarioKnowledgeBinding binding) {
        scenarioKnowledgeBindingMapper.insert(toEntity(binding));
        log.debug("持久化场景知识绑定, bindingId={}, scenarioCode={}", binding.bindingId(), binding.scenarioCode());
    }

    @Override
    public List<ScenarioKnowledgeBinding> findByScenarioCode(String scenarioCode) {
        LambdaQueryWrapper<ScenarioKnowledgeBindingEntity> queryWrapper = new LambdaQueryWrapper<ScenarioKnowledgeBindingEntity>()
                .eq(ScenarioKnowledgeBindingEntity::getTenantId, TenantContext.currentTenantId())
                .eq(ScenarioKnowledgeBindingEntity::getScenarioCode, scenarioCode)
                .orderByAsc(ScenarioKnowledgeBindingEntity::getPriority)
                .orderByAsc(ScenarioKnowledgeBindingEntity::getRecordedAt)
                .orderByDesc(ScenarioKnowledgeBindingEntity::getId);
        return scenarioKnowledgeBindingMapper.selectList(queryWrapper).stream()
                .map(this::toDomain)
                .toList();
    }

    private ScenarioKnowledgeBindingEntity toEntity(ScenarioKnowledgeBinding binding) {
        ScenarioKnowledgeBindingEntity entity = new ScenarioKnowledgeBindingEntity();
        entity.setTenantId(TenantIds.resolveId(binding.tenantCode()));
        entity.setBindingId(binding.bindingId());
        entity.setScenarioCode(binding.scenarioCode());
        entity.setKnowledgeCode(binding.knowledgeCode());
        entity.setPriority(binding.priority());
        entity.setEnabled(binding.enabled());
        entity.setRecordedAt(binding.recordedAt());
        return entity;
    }

    private ScenarioKnowledgeBinding toDomain(ScenarioKnowledgeBindingEntity entity) {
        return new ScenarioKnowledgeBinding(
                TenantIds.toCode(entity.getTenantId()),
                entity.getBindingId(),
                entity.getScenarioCode(),
                entity.getKnowledgeCode(),
                entity.getPriority(),
                Boolean.TRUE.equals(entity.getEnabled()),
                entity.getRecordedAt()
        );
    }
}
