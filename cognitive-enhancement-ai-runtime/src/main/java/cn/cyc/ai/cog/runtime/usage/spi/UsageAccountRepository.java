package cn.cyc.ai.cog.runtime.usage.spi;

import cn.cyc.ai.cog.runtime.usage.domain.UsageAccount;

import java.util.Optional;

/**
 * 用量额度账户仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface UsageAccountRepository {

    /**
     * 按租户查询账户。
     *
     * @param tenantCode 租户编码
     * @return 账户
     */
    Optional<UsageAccount> findByTenantCode(String tenantCode);

    /**
     * 保存账户。
     *
     * @param account 账户
     */
    void save(UsageAccount account);
}
