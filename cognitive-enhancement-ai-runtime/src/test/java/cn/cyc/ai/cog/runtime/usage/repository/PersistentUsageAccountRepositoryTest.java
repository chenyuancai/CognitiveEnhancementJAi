package cn.cyc.ai.cog.runtime.usage.repository;

import cn.cyc.ai.cog.runtime.usage.domain.UsageAccount;
import cn.cyc.ai.cog.runtime.usage.entity.UsageAccountEntity;
import cn.cyc.ai.cog.runtime.usage.mapper.UsageAccountMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 用量额度账户持久化仓储测试。
 *
 * @author cyc
 */
class PersistentUsageAccountRepositoryTest {

    @Test
    void save_shouldMapDomainToEntity() {
        UsageAccountMapper mapper = mock(UsageAccountMapper.class);
        PersistentUsageAccountRepository repository = new PersistentUsageAccountRepository(mapper);
        Instant updatedAt = Instant.parse("2026-06-16T00:00:00Z");
        UsageAccount account = new UsageAccount(
                "platform",
                new BigDecimal("9.500000"),
                new BigDecimal("0.500000"),
                true,
                updatedAt);

        repository.save(account);

        ArgumentCaptor<UsageAccountEntity> captor = ArgumentCaptor.forClass(UsageAccountEntity.class);
        verify(mapper).saveOrUpdateByTenantCode(captor.capture());
        UsageAccountEntity entity = captor.getValue();
        assertEquals(1L, entity.getTenantId());
        assertEquals(new BigDecimal("9.500000"), entity.getBalanceAmount());
        assertEquals(new BigDecimal("0.500000"), entity.getUsedAmount());
        assertEquals(Boolean.TRUE, entity.getEnabled());
        assertEquals(updatedAt, entity.getUpdatedAt());
    }

    @Test
    void findByTenantCode_shouldMapEntityToDomain() {
        UsageAccountMapper mapper = mock(UsageAccountMapper.class);
        PersistentUsageAccountRepository repository = new PersistentUsageAccountRepository(mapper);
        UsageAccountEntity entity = new UsageAccountEntity();
        entity.setTenantId(1L);
        entity.setBalanceAmount(new BigDecimal("8.750000"));
        entity.setUsedAmount(new BigDecimal("1.250000"));
        entity.setEnabled(true);
        entity.setUpdatedAt(Instant.parse("2026-06-16T01:00:00Z"));
        when(mapper.selectOne(any())).thenReturn(entity);

        Optional<UsageAccount> account = repository.findByTenantCode("platform");

        assertTrue(account.isPresent());
        assertEquals("platform", account.get().tenantCode());
        assertEquals(new BigDecimal("8.750000"), account.get().balanceAmount());
        assertEquals(new BigDecimal("1.250000"), account.get().usedAmount());
        assertTrue(account.get().enabled());
        assertEquals(Instant.parse("2026-06-16T01:00:00Z"), account.get().updatedAt());
    }
}
