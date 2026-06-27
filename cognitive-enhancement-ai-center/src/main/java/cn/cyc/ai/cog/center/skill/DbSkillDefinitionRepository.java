package cn.cyc.ai.cog.center.skill;

import cn.cyc.ai.cog.center.skill.entity.SkillDefinitionEntity;
import cn.cyc.ai.cog.center.skill.mapper.SkillDefinitionMapper;
import cn.cyc.ai.cog.center.support.JsonConverter;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 技能定义数据库仓储实现。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class DbSkillDefinitionRepository implements SkillDefinitionRepository {

    private final SkillDefinitionMapper mapper;

    public DbSkillDefinitionRepository(SkillDefinitionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<SkillDefinition> findByCode(String code) {
        QueryWrapper<SkillDefinitionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("skill_code", code);
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        SkillDefinitionEntity entity = mapper.selectOne(wrapper);
        return Optional.ofNullable(entity).map(this::toDefinition);
    }

    @Override
    public List<SkillDefinition> listAll() {
        QueryWrapper<SkillDefinitionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        return mapper.selectList(wrapper).stream()
                .sorted((a, b) -> a.getSkillCode().compareTo(b.getSkillCode()))
                .map(this::toDefinition)
                .toList();
    }

    @Override
    public SkillDefinition save(SkillDefinition definition) {
        QueryWrapper<SkillDefinitionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("skill_code", definition.code());
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        SkillDefinitionEntity existing = mapper.selectOne(wrapper);

        SkillDefinitionEntity entity = toEntity(definition);
        if (existing != null) {
            entity.setId(existing.getId());
            mapper.updateById(entity);
        } else {
            mapper.insert(entity);
        }
        return definition;
    }

    private SkillDefinition toDefinition(SkillDefinitionEntity e) {
        List<String> toolCodes = mapper.selectToolCodes(e.getSkillCode());
        return new SkillDefinition(
                e.getSkillCode(),
                e.getSkillName(),
                e.getSkillType(),
                e.getSkillInstruction(),
                toolCodes,
                RiskLevel.valueOf(e.getRiskLevel()),
                JsonConverter.stringListFromJson(e.getForbiddenRules()),
                JsonConverter.stringListFromJson(e.getExamples()),
                JsonConverter.stringListFromJson(e.getDependsOnSkillCodes()),
                CommonStatus.valueOf(e.getStatus())
        );
    }

    private SkillDefinitionEntity toEntity(SkillDefinition d) {
        SkillDefinitionEntity e = new SkillDefinitionEntity();
        e.setTenantId(TenantContext.currentTenantId());
        e.setSkillCode(d.skillCode());
        e.setSkillName(d.skillName());
        e.setSkillType(d.skillType());
        e.setSkillInstruction(d.skillInstruction());
        e.setRiskLevel(d.riskLevel().name());
        e.setForbiddenRules(JsonConverter.toJson(d.forbiddenRules()));
        e.setExamples(JsonConverter.toJson(d.examples()));
        e.setDependsOnSkillCodes(JsonConverter.toJson(d.dependsOnSkillCodes()));
        e.setStatus(d.status().name());
        return e;
    }
}
