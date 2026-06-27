package cn.cyc.ai.cog.runtime.usage.repository;

import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.security.TenantIds;
import cn.cyc.ai.cog.runtime.usage.domain.UsageAccount;
import cn.cyc.ai.cog.runtime.usage.entity.UsageAccountEntity;
import cn.cyc.ai.cog.runtime.usage.mapper.UsageAccountMapper;
import cn.cyc.ai.cog.runtime.usage.spi.UsageAccountRepository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

/**
 * 基于 MyBatis Plus 的用量额度账户仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentUsageAccountRepository implements UsageAccountRepository {

    /**
     * 账户 Mapper。
     */
    private final UsageAccountMapper usageAccountMapper;

    /**
     * 构造用量额度账户持久化仓储。
     *
     * @param usageAccountMapper 账户 Mapper
     */
    public PersistentUsageAccountRepository(UsageAccountMapper usageAccountMapper) {
        this.usageAccountMapper = usageAccountMapper;
    }

    @Override
    public Optional<UsageAccount> findByTenantCode(String tenantCode) {
        String normalizedTenantCode = TenantContext.normalize(tenantCode);
        LambdaQueryWrapper<UsageAccountEntity> queryWrapper = new LambdaQueryWrapper<UsageAccountEntity>()
                .eq(UsageAccountEntity::getTenantId, TenantIds.resolveId(normalizedTenantCode));
        return Optional.ofNullable(usageAccountMapper.selectOne(queryWrapper))
                .map(this::toDomain);
    }

    @Override
    public void save(UsageAccount account) {
        usageAccountMapper.saveOrUpdateByTenantCode(toEntity(account));
    }

    private UsageAccountEntity toEntity(UsageAccount account) {
        UsageAccountEntity entity = new UsageAccountEntity();
        entity.setTenantId(TenantIds.resolveId(account.tenantCode()));
        entity.setBalanceAmount(account.balanceAmount());
        entity.setUsedAmount(account.usedAmount());
        entity.setEnabled(account.enabled());
        entity.setUpdatedAt(account.updatedAt());
        return entity;
    }

    private UsageAccount toDomain(UsageAccountEntity entity) {
        return new UsageAccount(
                TenantIds.toCode(entity.getTenantId()),
                normalizeAmount(entity.getBalanceAmount()),
                normalizeAmount(entity.getUsedAmount()),
                entity.getEnabled() == null || entity.getEnabled(),
                entity.getUpdatedAt() == null ? Instant.now() : entity.getUpdatedAt()
        );
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
