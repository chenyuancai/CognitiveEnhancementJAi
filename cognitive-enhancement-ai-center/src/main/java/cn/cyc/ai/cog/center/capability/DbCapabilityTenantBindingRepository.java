package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.center.capability.entity.CapabilityTenantBindingEntity;
import cn.cyc.ai.cog.center.capability.mapper.CapabilityTenantBindingMapper;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityTenantBinding;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityTenantBindingRepository;
import cn.cyc.ai.cog.runtime.security.TenantIds;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Capability 租户启停绑定数据库仓储。
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class DbCapabilityTenantBindingRepository implements CapabilityTenantBindingRepository {

    private final CapabilityTenantBindingMapper mapper;

    public DbCapabilityTenantBindingRepository(CapabilityTenantBindingMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<CapabilityTenantBinding> findByTenantAndCapability(String tenantCode, String capabilityCode) {
        QueryWrapper<CapabilityTenantBindingEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantIds.resolveId(tenantCode));
        wrapper.eq("capability_code", capabilityCode);
        return Optional.ofNullable(mapper.selectOne(wrapper)).map(this::toDomain);
    }

    @Override
    public CapabilityTenantBinding save(CapabilityTenantBinding binding) {
        QueryWrapper<CapabilityTenantBindingEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantIds.resolveId(binding.tenantCode()));
        wrapper.eq("capability_code", binding.capabilityCode());
        CapabilityTenantBindingEntity existing = mapper.selectOne(wrapper);

        CapabilityTenantBindingEntity entity = toEntity(binding);
        if (existing != null) {
            entity.setId(existing.getId());
            mapper.updateById(entity);
        } else {
            mapper.insert(entity);
        }
        return binding;
    }

    private CapabilityTenantBinding toDomain(CapabilityTenantBindingEntity entity) {
        return new CapabilityTenantBinding(
                TenantIds.toCode(entity.getTenantId()),
                entity.getCapabilityCode(),
                entity.getEnabled() != null && entity.getEnabled() == 1
        );
    }

    private CapabilityTenantBindingEntity toEntity(CapabilityTenantBinding binding) {
        CapabilityTenantBindingEntity entity = new CapabilityTenantBindingEntity();
        entity.setTenantId(TenantIds.resolveId(binding.tenantCode()));
        entity.setCapabilityCode(binding.capabilityCode());
        entity.setEnabled(binding.enabled() ? 1 : 0);
        return entity;
    }
}
