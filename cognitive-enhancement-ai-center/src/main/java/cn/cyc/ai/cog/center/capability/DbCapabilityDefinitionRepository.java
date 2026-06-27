package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.center.capability.entity.CapabilityDefinitionEntity;
import cn.cyc.ai.cog.center.capability.mapper.CapabilityDefinitionMapper;
import cn.cyc.ai.cog.center.support.JsonConverter;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityLifecycleStatus;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 能力定义数据库仓储实现。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class DbCapabilityDefinitionRepository implements CapabilityDefinitionRepository {

    private final CapabilityDefinitionMapper mapper;

    public DbCapabilityDefinitionRepository(CapabilityDefinitionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<CapabilityDefinition> findByCode(String code) {
        return findPublishedByCapabilityCode(code);
    }

    @Override
    public List<CapabilityDefinition> listAll() {
        QueryWrapper<CapabilityDefinitionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        return mapper.selectList(wrapper).stream()
                .sorted(Comparator.comparing(CapabilityDefinitionEntity::getCapabilityCode)
                        .thenComparing(CapabilityDefinitionEntity::getVersion))
                .map(this::toDefinition)
                .toList();
    }

    @Override
    public List<CapabilityDefinition> listVersionsByCapabilityCode(String capabilityCode) {
        QueryWrapper<CapabilityDefinitionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        wrapper.eq("capability_code", capabilityCode);
        return mapper.selectList(wrapper).stream()
                .sorted(Comparator.comparing(CapabilityDefinitionEntity::getVersion))
                .map(this::toDefinition)
                .toList();
    }

    @Override
    public CapabilityDefinition save(CapabilityDefinition definition) {
        QueryWrapper<CapabilityDefinitionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("capability_code", definition.code());
        wrapper.eq("version", definition.version());
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        CapabilityDefinitionEntity existing = mapper.selectOne(wrapper);

        CapabilityDefinitionEntity entity = toEntity(definition);
        if (existing != null) {
            entity.setId(existing.getId());
            mapper.updateById(entity);
        } else {
            mapper.insert(entity);
        }
        return definition;
    }

    private CapabilityDefinition toDefinition(CapabilityDefinitionEntity e) {
        CapabilityLifecycleStatus lifecycleStatus = e.getLifecycleStatus() == null
                ? CapabilityLifecycleStatus.DRAFT
                : CapabilityLifecycleStatus.valueOf(e.getLifecycleStatus());
        return new CapabilityDefinition(
                e.getCapabilityCode(),
                e.getCapabilityName(),
                e.getCapabilityDesc(),
                JsonConverter.fromJson(e.getInputSchema(), SchemaDefinition.class),
                JsonConverter.fromJson(e.getOutputSchema(), SchemaDefinition.class),
                JsonConverter.mapFromJson(e.getParameterConstraints(), ParameterConstraintDefinition.class),
                ExecutionMode.valueOf(e.getExecuteMode()),
                e.getBoundAgentCode(),
                RiskLevel.valueOf(e.getRiskLevel()),
                e.getNeedHumanConfirm() != null && e.getNeedHumanConfirm() == 1,
                CommonStatus.valueOf(e.getStatus()),
                e.getVersion(),
                e.getPublishedAt() != null ? e.getPublishedAt().toInstant(java.time.ZoneOffset.UTC) : null,
                lifecycleStatus
        );
    }

    private CapabilityDefinitionEntity toEntity(CapabilityDefinition d) {
        CapabilityDefinitionEntity e = new CapabilityDefinitionEntity();
        e.setTenantId(TenantContext.currentTenantId());
        e.setCapabilityCode(d.capabilityCode());
        e.setCapabilityName(d.capabilityName());
        e.setCapabilityDesc(d.capabilityDesc());
        e.setVersion(d.version());
        e.setInputSchema(JsonConverter.toJson(d.inputSchema()));
        e.setOutputSchema(JsonConverter.toJson(d.outputSchema()));
        e.setParameterConstraints(JsonConverter.toJson(d.parameterConstraints()));
        e.setExecuteMode(d.executeMode().name());
        e.setBoundAgentCode(d.boundAgentCode());
        e.setRiskLevel(d.riskLevel().name());
        e.setNeedHumanConfirm(d.needHumanConfirm() ? 1 : 0);
        e.setStatus(d.status().name());
        e.setLifecycleStatus(d.lifecycleStatus().name());
        e.setPublishedAt(d.publishedAt() != null
                ? java.time.LocalDateTime.ofInstant(d.publishedAt(), java.time.ZoneOffset.UTC)
                : null);
        return e;
    }
}
