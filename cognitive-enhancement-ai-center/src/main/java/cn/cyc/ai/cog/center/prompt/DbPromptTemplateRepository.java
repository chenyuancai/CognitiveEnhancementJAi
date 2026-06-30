package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.center.prompt.entity.PromptTemplateEntity;
import cn.cyc.ai.cog.center.prompt.mapper.PromptTemplateMapper;
import cn.cyc.ai.cog.center.support.JsonConverter;
import cn.cyc.ai.cog.core.metadata.prompt.PromptLifecycleStatus;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplateRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
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
 * 提示词模板数据库仓储实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class DbPromptTemplateRepository implements PromptTemplateRepository {

    /** Mapper。 */
    private final PromptTemplateMapper mapper;

    /**
     * 创建Db提示词Template仓储。
     *
     * @param mapper Mapper
     */
    public DbPromptTemplateRepository(PromptTemplateMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 查找人编码。
     *
     * @param code 编码
     * @return 查找结果
     */
    @Override
    public Optional<PromptTemplate> findByCode(String code) {
        return findPublishedByPromptCode(code);
    }

    /**
     * 查询All列表。
     * @return 结果列表
     */
    @Override
    public List<PromptTemplate> listAll() {
        QueryWrapper<PromptTemplateEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        return mapper.selectList(wrapper).stream()
                .sorted(Comparator.comparing(PromptTemplateEntity::getPromptCode)
                        .thenComparing(PromptTemplateEntity::getVersion))
                .map(this::toDefinition)
                .toList();
    }

    /**
     * 查询Versions人提示词编码列表。
     *
     * @param promptCode 提示词编码
     * @return 结果列表
     */
    @Override
    public List<PromptTemplate> listVersionsByPromptCode(String promptCode) {
        QueryWrapper<PromptTemplateEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        wrapper.eq("prompt_code", promptCode);
        return mapper.selectList(wrapper).stream()
                .sorted(Comparator.comparing(PromptTemplateEntity::getVersion))
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
    public PromptTemplate save(PromptTemplate definition) {
        QueryWrapper<PromptTemplateEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("prompt_code", definition.promptCode());
        wrapper.eq("version", definition.version());
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        PromptTemplateEntity existing = mapper.selectOne(wrapper);

        PromptTemplateEntity entity = toEntity(definition);
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
    private PromptTemplate toDefinition(PromptTemplateEntity e) {
        PromptLifecycleStatus lifecycleStatus = e.getLifecycleStatus() == null
                ? PromptLifecycleStatus.DRAFT
                : PromptLifecycleStatus.valueOf(e.getLifecycleStatus());
        return new PromptTemplate(
                e.getPromptCode(),
                e.getPromptName(),
                e.getScenarioCode(),
                e.getVersion(),
                e.getTemplateContent(),
                JsonConverter.fromJson(e.getVariableSchema(), SchemaDefinition.class),
                JsonConverter.fromJson(e.getOutputSchema(), SchemaDefinition.class),
                CommonStatus.valueOf(e.getStatus()),
                e.getPublishedAt() != null ? e.getPublishedAt().toInstant(java.time.ZoneOffset.UTC) : null,
                lifecycleStatus
        );
    }

    /**
     * 转换为实体。
     *
     * @param d d
     * @return 转换结果
     */
    private PromptTemplateEntity toEntity(PromptTemplate d) {
        PromptTemplateEntity e = new PromptTemplateEntity();
        e.setTenantId(TenantContext.currentTenantId());
        e.setPromptCode(d.promptCode());
        e.setPromptName(d.promptName());
        e.setScenarioCode(d.scenarioCode());
        e.setVersion(d.version());
        e.setTemplateContent(d.templateContent());
        e.setVariableSchema(JsonConverter.toJson(d.variableSchema()));
        e.setOutputSchema(JsonConverter.toJson(d.outputSchema()));
        e.setStatus(d.status().name());
        e.setLifecycleStatus(d.lifecycleStatus().name());
        e.setPublishedAt(d.publishedAt() != null
                ? java.time.LocalDateTime.ofInstant(d.publishedAt(), java.time.ZoneOffset.UTC)
                : null);
        return e;
    }
}
