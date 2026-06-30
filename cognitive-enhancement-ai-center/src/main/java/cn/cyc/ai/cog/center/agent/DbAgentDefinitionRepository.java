package cn.cyc.ai.cog.center.agent;

import cn.cyc.ai.cog.center.agent.entity.AgentDefinitionEntity;
import cn.cyc.ai.cog.center.agent.mapper.AgentDefinitionMapper;
import cn.cyc.ai.cog.center.support.JsonConverter;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Agent 定义数据库仓储实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class DbAgentDefinitionRepository implements AgentDefinitionRepository {

    /** Mapper。 */
    private final AgentDefinitionMapper mapper;

    /**
     * 创建Db智能体Definition仓储。
     *
     * @param mapper Mapper
     */
    public DbAgentDefinitionRepository(AgentDefinitionMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 查找人编码。
     *
     * @param code 编码
     * @return 查找结果
     */
    @Override
    public Optional<AgentDefinition> findByCode(String code) {
        QueryWrapper<AgentDefinitionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("agent_code", code);
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        AgentDefinitionEntity entity = mapper.selectOne(wrapper);
        return Optional.ofNullable(entity).map(this::toDefinition);
    }

    /**
     * 查询All列表。
     * @return 结果列表
     */
    @Override
    public List<AgentDefinition> listAll() {
        QueryWrapper<AgentDefinitionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        return mapper.selectList(wrapper).stream()
                .sorted((a, b) -> a.getAgentCode().compareTo(b.getAgentCode()))
                .map(this::toDefinition)
                .toList();
    }

    /**
     * 执行save。
     *
     * @param definition definition
     * @return 执行结果
     */
    @Override
    public AgentDefinition save(AgentDefinition definition) {
        QueryWrapper<AgentDefinitionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("agent_code", definition.code());
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        AgentDefinitionEntity existing = mapper.selectOne(wrapper);

        AgentDefinitionEntity entity = toEntity(definition);
        if (existing != null) {
            entity.setId(existing.getId());
            mapper.updateById(entity);
        } else {
            mapper.insert(entity);
        }
        return definition;
    }

    /**
     * 转换为Definition。
     *
     * @param e e
     * @return 转换结果
     */
    private AgentDefinition toDefinition(AgentDefinitionEntity e) {
        List<String> skillCodes = mapper.selectSkillCodes(e.getAgentCode());
        return new AgentDefinition(
                e.getAgentCode(),
                e.getAgentName(),
                e.getRoleDesc(),
                e.getGoalDesc(),
                e.getModelCode(),
                e.getMaxSteps(),
                e.getMaxCost(),
                e.getTimeoutMs(),
                skillCodes,
                JsonConverter.mapFromJson(e.getParameterConstraints(), ParameterConstraintDefinition.class),
                CommonStatus.valueOf(e.getStatus())
        );
    }

    /**
     * 转换为实体。
     *
     * @param d d
     * @return 转换结果
     */
    private AgentDefinitionEntity toEntity(AgentDefinition d) {
        AgentDefinitionEntity e = new AgentDefinitionEntity();
        e.setTenantId(TenantContext.currentTenantId());
        e.setAgentCode(d.agentCode());
        e.setAgentName(d.agentName());
        e.setRoleDesc(d.roleDesc());
        e.setGoalDesc(d.goalDesc());
        e.setModelCode(d.modelCode());
        e.setMaxSteps(d.maxSteps());
        e.setMaxCost(d.maxCost());
        e.setTimeoutMs(d.timeoutMs());
        e.setParameterConstraints(JsonConverter.toJson(d.parameterConstraints()));
        e.setStatus(d.status().name());
        return e;
    }
}
