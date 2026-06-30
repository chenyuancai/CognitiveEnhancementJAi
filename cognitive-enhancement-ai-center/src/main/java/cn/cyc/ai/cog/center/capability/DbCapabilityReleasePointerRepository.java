package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.center.capability.entity.CapabilityReleasePointerEntity;
import cn.cyc.ai.cog.center.capability.mapper.CapabilityReleasePointerMapper;
import cn.cyc.ai.cog.center.support.JsonConverter;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityReleasePointer;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityReleasePointerRepository;
import cn.cyc.ai.cog.core.metadata.prompt.PromptGrayRule;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.security.TenantIds;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Capability 发布指针数据库仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class DbCapabilityReleasePointerRepository implements CapabilityReleasePointerRepository {

    /** Mapper。 */
    private final CapabilityReleasePointerMapper mapper;

    /**
     * 创建Db能力ReleasePointer仓储。
     *
     * @param mapper Mapper
     */
    public DbCapabilityReleasePointerRepository(CapabilityReleasePointerMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 查找人能力编码。
     *
     * @param capabilityCode 能力编码
     * @return 查找结果
     */
    @Override
    public Optional<CapabilityReleasePointer> findByCapabilityCode(String capabilityCode) {
        QueryWrapper<CapabilityReleasePointerEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        wrapper.eq("capability_code", capabilityCode);
        return Optional.ofNullable(mapper.selectOne(wrapper)).map(this::toDomain);
    }

    /**
     * 执行save。
     *
     * @param pointer pointer
     * @return 执行结果
     */
    @Override
    public CapabilityReleasePointer save(CapabilityReleasePointer pointer) {
        QueryWrapper<CapabilityReleasePointerEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantIds.resolveId(pointer.tenantCode()));
        wrapper.eq("capability_code", pointer.capabilityCode());
        CapabilityReleasePointerEntity existing = mapper.selectOne(wrapper);

        CapabilityReleasePointerEntity entity = toEntity(pointer);
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
    private CapabilityReleasePointer toDomain(CapabilityReleasePointerEntity entity) {
        PromptGrayRule grayRule = entity.getGrayRuleJson() == null
                ? null
                : JsonConverter.fromJson(entity.getGrayRuleJson(), PromptGrayRule.class);
        return new CapabilityReleasePointer(
                TenantIds.toCode(entity.getTenantId()),
                entity.getCapabilityCode(),
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
    private CapabilityReleasePointerEntity toEntity(CapabilityReleasePointer pointer) {
        CapabilityReleasePointerEntity entity = new CapabilityReleasePointerEntity();
        entity.setTenantId(TenantIds.resolveId(pointer.tenantCode()));
        entity.setCapabilityCode(pointer.capabilityCode());
        entity.setBaselineVersion(pointer.baselineVersion());
        entity.setCandidateVersion(pointer.candidateVersion());
        entity.setGrayRuleJson(pointer.grayRule() == null ? null : JsonConverter.toJson(pointer.grayRule()));
        return entity;
    }
}
