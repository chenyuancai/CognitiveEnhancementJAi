package cn.cyc.ai.cog.runtime.usage.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.usage.domain.UsageAccount;
import cn.cyc.ai.cog.runtime.usage.repository.InMemoryUsageAccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 默认用量账户服务测试。
 *
 * @author cyc
 */
class DefaultRuntimeUsageAccountServiceTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldRejectWhenAccountBalanceIsInsufficient() {
        TenantContext.setTenantCode("tenant-a");
        RuntimeUsageProperties properties = new RuntimeUsageProperties();
        properties.getAccount().setEnabled(true);
        properties.getAccount().setPreflightCostAmount(new BigDecimal("1.000000"));
        InMemoryUsageAccountRepository repository = new InMemoryUsageAccountRepository();
        repository.save(new UsageAccount("tenant-a", new BigDecimal("0.500000"), BigDecimal.ZERO, true, Instant.now()));
        DefaultRuntimeUsageAccountService service = new DefaultRuntimeUsageAccountService(repository, properties);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.checkBeforeExecution("capability.qa.answer"));

        assertEquals("TOO_MANY_REQUESTS", exception.getSemanticCode());
        assertEquals("租户额度余额不足: tenant-a", exception.getMessage());
    }

    @Test
    void shouldDeductUsageCostFromTenantAccount() {
        TenantContext.setTenantCode("tenant-a");
        RuntimeUsageProperties properties = new RuntimeUsageProperties();
        properties.getAccount().setEnabled(true);
        InMemoryUsageAccountRepository repository = new InMemoryUsageAccountRepository();
        repository.save(new UsageAccount("tenant-a", new BigDecimal("10.000000"), BigDecimal.ZERO, true, Instant.now()));
        DefaultRuntimeUsageAccountService service = new DefaultRuntimeUsageAccountService(repository, properties);
        UsageRecord usageRecord = new UsageRecord(
                "trace-usage-account-001",
                "tenant-a",
                "capability.qa.answer",
                "agent.qa",
                "TOOL",
                null,
                "tool.search",
                0,
                0,
                0,
                new BigDecimal("1.250000"),
                Instant.now()
        );

        UsageAccount account = service.recordUsage(usageRecord);

        assertEquals(0, new BigDecimal("8.750000").compareTo(account.balanceAmount()));
        assertEquals(0, new BigDecimal("1.250000").compareTo(account.usedAmount()));
    }
}
