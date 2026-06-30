package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.center.prompt.entity.PromptReleasePointerEntity;
import cn.cyc.ai.cog.center.prompt.mapper.PromptReleasePointerMapper;
import cn.cyc.ai.cog.center.support.JsonConverter;
import cn.cyc.ai.cog.core.metadata.prompt.PromptGrayRule;
import cn.cyc.ai.cog.core.metadata.prompt.PromptReleasePointer;
import cn.cyc.ai.cog.core.metadata.prompt.PromptReleasePointerRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.security.TenantIds;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Prompt 发布指针数据库仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class DbPromptReleasePointerRepository implements PromptReleasePointerRepository {

    /** Mapper。 */
    private final PromptReleasePointerMapper mapper;

    /**
     * 创建Db提示词ReleasePointer仓储。
     *
     * @param mapper Mapper
     */
    public DbPromptReleasePointerRepository(PromptReleasePointerMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 查找人提示词编码。
     *
     * @param promptCode 提示词编码
     * @return 查找结果
     */
    @Override
    public Optional<PromptReleasePointer> findByPromptCode(String promptCode) {
        QueryWrapper<PromptReleasePointerEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        wrapper.eq("prompt_code", promptCode);
        return Optional.ofNullable(mapper.selectOne(wrapper)).map(this::toDomain);
    }

    /**
     * 执行save。
     *
     * @param pointer pointer
     * @return 执行结果
     */
    @Override
    public PromptReleasePointer save(PromptReleasePointer pointer) {
        QueryWrapper<PromptReleasePointerEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        wrapper.eq("prompt_code", pointer.promptCode());
        PromptReleasePointerEntity existing = mapper.selectOne(wrapper);

        PromptReleasePointerEntity entity = toEntity(pointer);
        if (existing != null) {
            entity.setId(existing.getId());
            mapper.updateById(entity);
        } else {
            mapper.insert(entity);
        }
        return pointer;
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private PromptReleasePointer toDomain(PromptReleasePointerEntity entity) {
        PromptGrayRule grayRule = entity.getGrayRuleJson() == null
                ? null
                : JsonConverter.fromJson(entity.getGrayRuleJson(), PromptGrayRule.class);
        return new PromptReleasePointer(
                TenantIds.toCode(entity.getTenantId()),
                entity.getPromptCode(),
                entity.getBaselineVersion(),
                entity.getCandidateVersion(),
                grayRule
        );
    }

    /**
     * 转换为实体。
     *
     * @param pointer pointer
     * @return 转换结果
     */
    private PromptReleasePointerEntity toEntity(PromptReleasePointer pointer) {
        PromptReleasePointerEntity entity = new PromptReleasePointerEntity();
        entity.setTenantId(TenantIds.resolveId(pointer.tenantCode()));
        entity.setPromptCode(pointer.promptCode());
        entity.setBaselineVersion(pointer.baselineVersion());
        entity.setCandidateVersion(pointer.candidateVersion());
        entity.setGrayRuleJson(pointer.grayRule() == null ? null : JsonConverter.toJson(pointer.grayRule()));
        return entity;
    }
}
