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
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class DbCapabilityReleasePointerRepository implements CapabilityReleasePointerRepository {

    private final CapabilityReleasePointerMapper mapper;

    public DbCapabilityReleasePointerRepository(CapabilityReleasePointerMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<CapabilityReleasePointer> findByCapabilityCode(String capabilityCode) {
        QueryWrapper<CapabilityReleasePointerEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantContext.currentTenantId());
        wrapper.eq("capability_code", capabilityCode);
        return Optional.ofNullable(mapper.selectOne(wrapper)).map(this::toDomain);
    }

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
