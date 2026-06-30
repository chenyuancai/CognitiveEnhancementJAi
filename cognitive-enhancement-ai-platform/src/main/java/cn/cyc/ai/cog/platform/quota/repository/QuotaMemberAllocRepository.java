package cn.cyc.ai.cog.platform.quota.repository;

import cn.cyc.ai.cog.platform.quota.domain.QuotaMemberAlloc;
import cn.cyc.ai.cog.platform.quota.dto.QuotaMemberAllocSaveRequest;

import java.util.List;

/**
 * 额度MemberAlloc仓储
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface QuotaMemberAllocRepository {

    List<QuotaMemberAlloc> listByAccount(Long accountId);

    QuotaMemberAlloc findByAccountAndUser(Long accountId, Long userId);

    QuotaMemberAlloc allocate(Long accountId, QuotaMemberAllocSaveRequest request);

    void remove(Long accountId, Long userId);

    void updateUsedAmount(QuotaMemberAlloc alloc);
}
