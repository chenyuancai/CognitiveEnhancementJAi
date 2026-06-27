package cn.cyc.ai.cog.runtime.usage.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.usage.domain.UsageAccount;
import cn.cyc.ai.cog.runtime.usage.spi.RuntimeUsageAccountService;
import cn.cyc.ai.cog.runtime.usage.spi.UsageAccountRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 默认运行时用量额度账户服务。
 *
 * @author cyc
 */
@Service
@ConditionalOnProperty(name = "cog.runtime.usage.account.backend", havingValue = "legacy", matchIfMissing = true)
public class DefaultRuntimeUsageAccountService implements RuntimeUsageAccountService {

    /**
     * 账户仓储。
     */
    private final UsageAccountRepository usageAccountRepository;

    /**
     * 用量配置。
     */
    private final RuntimeUsageProperties properties;

    /**
     * 构造默认账户服务。
     *
     * @param usageAccountRepository 账户仓储
     * @param properties             用量配置
     */
    public DefaultRuntimeUsageAccountService(UsageAccountRepository usageAccountRepository,
                                             RuntimeUsageProperties properties) {
        this.usageAccountRepository = usageAccountRepository;
        this.properties = properties;
    }

    @Override
    public void checkBeforeExecution(String capabilityCode) {
        if (!properties.getAccount().isEnabled()) {
            return;
        }
        UsageAccount account = currentAccount();
        if (!account.enabled()) {
            return;
        }
        if (!account.hasEnoughBalance(properties.getAccount().getPreflightCostAmount())) {
            throw new BusinessException("TOO_MANY_REQUESTS", "租户额度余额不足: " + account.tenantCode());
        }
    }

    @Override
    public UsageAccount recordUsage(UsageRecord usageRecord) {
        if (!properties.getAccount().isEnabled()) {
            return currentAccount();
        }
        BigDecimal costAmount = normalizeAmount(usageRecord.estimatedCostAmount());
        if (costAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return currentAccount();
        }
        UsageAccount account = currentAccount();
        if (!account.enabled()) {
            return account;
        }
        UsageAccount updated = account.deduct(costAmount);
        usageAccountRepository.save(updated);
        return updated;
    }

    @Override
    public UsageAccount currentAccount() {
        String tenantCode = TenantContext.currentTenantCode();
        return usageAccountRepository.findByTenantCode(tenantCode)
                .orElseGet(() -> createDefaultAccount(tenantCode));
    }

    private UsageAccount createDefaultAccount(String tenantCode) {
        UsageAccount account = new UsageAccount(
                tenantCode,
                normalizeAmount(properties.getAccount().getDefaultBalanceAmount()),
                BigDecimal.ZERO,
                true,
                Instant.now()
        );
        usageAccountRepository.save(account);
        return account;
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
