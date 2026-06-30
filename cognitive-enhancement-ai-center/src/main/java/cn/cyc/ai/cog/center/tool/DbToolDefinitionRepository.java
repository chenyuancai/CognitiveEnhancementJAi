package cn.cyc.ai.cog.center.tool;

import cn.cyc.ai.cog.center.tool.entity.ToolDefinitionEntity;
import cn.cyc.ai.cog.center.tool.mapper.ToolDefinitionMapper;
import cn.cyc.ai.cog.center.support.JsonConverter;
import cn.cyc.ai.cog.core.metadata.tool.RetryPolicy;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 工具定义数据库仓储实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class DbToolDefinitionRepository implements ToolDefinitionRepository {

    /** Mapper。 */
    private final ToolDefinitionMapper mapper;

    /**
     * 创建Db工具Definition仓储。
     *
     * @param mapper Mapper
     */
    public DbToolDefinitionRepository(ToolDefinitionMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 查找人编码。
     *
     * @param code 编码
     * @return 查找结果
     */
    @Override
    public Optional<ToolDefinition> findByCode(String code) {
        QueryWrapper<ToolDefinitionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tool_code", code);
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        ToolDefinitionEntity entity = mapper.selectOne(wrapper);
        return Optional.ofNullable(entity).map(this::toDefinition);
    }

    /**
     * 查询All列表。
     * @return 结果列表
     */
    @Override
    public List<ToolDefinition> listAll() {
        QueryWrapper<ToolDefinitionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        return mapper.selectList(wrapper).stream()
                .sorted((a, b) -> a.getToolCode().compareTo(b.getToolCode()))
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
    public ToolDefinition save(ToolDefinition definition) {
        QueryWrapper<ToolDefinitionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tool_code", definition.code());
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        ToolDefinitionEntity existing = mapper.selectOne(wrapper);

        ToolDefinitionEntity entity = toEntity(definition);
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
    private ToolDefinition toDefinition(ToolDefinitionEntity e) {
        return new ToolDefinition(
                e.getToolCode(),
                e.getToolName(),
                ToolProtocolType.valueOf(e.getProtocolType()),
                JsonConverter.fromJson(e.getRequestSchema(), SchemaDefinition.class),
                JsonConverter.fromJson(e.getResponseSchema(), SchemaDefinition.class),
                e.getPermissionScope(),
                e.getRiskLevel() == null ? RiskLevel.LOW : RiskLevel.valueOf(e.getRiskLevel()),
                e.getTimeoutMs(),
                new RetryPolicy(e.getRetryMaxAttempts()),
                e.getImplRef(),
                CommonStatus.valueOf(e.getStatus())
        );
    }

    /**
     * 转换为实体。
     *
     * @param d d
     * @return 转换结果
     */
    private ToolDefinitionEntity toEntity(ToolDefinition d) {
        ToolDefinitionEntity e = new ToolDefinitionEntity();
        e.setTenantId(TenantContext.currentTenantId());
        e.setToolCode(d.toolCode());
        e.setToolName(d.toolName());
        e.setProtocolType(d.protocolType().name());
        e.setRequestSchema(JsonConverter.toJson(d.requestSchema()));
        e.setResponseSchema(JsonConverter.toJson(d.responseSchema()));
        e.setPermissionScope(d.permissionScope());
        e.setRiskLevel(d.riskLevel().name());
        e.setTimeoutMs(d.timeoutMs());
        e.setRetryMaxAttempts(d.retryPolicy().maxAttempts());
        e.setImplRef(d.implRef());
        e.setStatus(d.status().name());
        return e;
    }
}
