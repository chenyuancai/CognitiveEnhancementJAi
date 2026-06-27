package cn.cyc.ai.cog.platform.quota.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.quota.domain.TokenRecord;

import java.time.LocalDateTime;
import java.util.List;

public interface TokenRecordRepository {

    PageResult<TokenRecord> page(long current, long size, Long accountId);

    TokenRecord findByIdempotencyKey(String idempotencyKey);

    void insert(TokenRecord record);

    long countByTenantAndTypeAndTimeRange(Long tenantId, String recordType, LocalDateTime start, LocalDateTime end);

    List<TokenRecord> listByTenantAndTimeRange(Long tenantId, LocalDateTime start, LocalDateTime end);
}
