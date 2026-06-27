package cn.cyc.ai.cog.runtime.usage.repository;

import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.usage.domain.UsageAccount;
import cn.cyc.ai.cog.runtime.usage.spi.UsageAccountRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存用量额度账户仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryUsageAccountRepository implements UsageAccountRepository {

    /**
     * 按租户保存账户。
     */
    private final Map<String, UsageAccount> accountByTenant = new ConcurrentHashMap<>();

    @Override
    public Optional<UsageAccount> findByTenantCode(String tenantCode) {
        return Optional.ofNullable(accountByTenant.get(TenantContext.normalize(tenantCode)));
    }

    @Override
    public void save(UsageAccount account) {
        accountByTenant.put(account.tenantCode(), account);
    }
}
