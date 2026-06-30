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
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class DbCapabilityTenantBindingRepository implements CapabilityTenantBindingRepository {

    /** Mapper。 */
    private final CapabilityTenantBindingMapper mapper;

    /**
     * 创建Db能力租户Binding仓储。
     *
     * @param mapper Mapper
     */
    public DbCapabilityTenantBindingRepository(CapabilityTenantBindingMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 查找人租户And能力。
     *
     * @param tenantCode 租户编码
     * @param capabilityCode 能力编码
     * @return 查找结果
     */
    @Override
    public Optional<CapabilityTenantBinding> findByTenantAndCapability(String tenantCode, String capabilityCode) {
        QueryWrapper<CapabilityTenantBindingEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", TenantIds.resolveId(tenantCode));
        wrapper.eq("capability_code", capabilityCode);
        return Optional.ofNullable(mapper.selectOne(wrapper)).map(this::toDomain);
    }

    /**
     * 执行save。
     *
     * @param binding binding
     * @return 执行结果
     */
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

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private CapabilityTenantBinding toDomain(CapabilityTenantBindingEntity entity) {
        return new CapabilityTenantBinding(
                TenantIds.toCode(entity.getTenantId()),
                entity.getCapabilityCode(),
                entity.getEnabled() != null && entity.getEnabled() == 1
        );
    }

    /**
     * 转换为实体。
     *
     * @param binding binding
     * @return 转换结果
     */
    private CapabilityTenantBindingEntity toEntity(CapabilityTenantBinding binding) {
        CapabilityTenantBindingEntity entity = new CapabilityTenantBindingEntity();
        entity.setTenantId(TenantIds.resolveId(binding.tenantCode()));
        entity.setCapabilityCode(binding.capabilityCode());
        entity.setEnabled(binding.enabled() ? 1 : 0);
        return entity;
    }
}
