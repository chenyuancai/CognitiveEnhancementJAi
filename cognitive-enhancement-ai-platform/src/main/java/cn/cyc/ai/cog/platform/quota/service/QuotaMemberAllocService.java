package cn.cyc.ai.cog.platform.quota.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.platform.quota.domain.QuotaMemberAlloc;
import cn.cyc.ai.cog.platform.quota.dto.QuotaMemberAllocSaveRequest;
import cn.cyc.ai.cog.platform.quota.repository.QuotaMemberAllocRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuotaMemberAllocService {

    private final QuotaMemberAllocRepository quotaMemberAllocRepository;

    public QuotaMemberAllocService(QuotaMemberAllocRepository quotaMemberAllocRepository) {
        this.quotaMemberAllocRepository = quotaMemberAllocRepository;
    }

    public List<QuotaMemberAlloc> listByAccount(Long accountId) {
        return quotaMemberAllocRepository.listByAccount(accountId);
    }

    public QuotaMemberAlloc getByAccountAndUser(Long accountId, Long userId) {
        return quotaMemberAllocRepository.findByAccountAndUser(accountId, userId);
    }

    @Transactional
    public QuotaMemberAlloc allocate(Long accountId, QuotaMemberAllocSaveRequest request) {
        return quotaMemberAllocRepository.allocate(accountId, request);
    }

    @Transactional
    public void remove(Long accountId, Long userId) {
        quotaMemberAllocRepository.remove(accountId, userId);
    }

    public void assertMemberQuota(Long accountId, Long userId, long amount) {
        if (userId == null || amount <= 0) {
            return;
        }
        QuotaMemberAlloc alloc = quotaMemberAllocRepository.findByAccountAndUser(accountId, userId);
        if (alloc == null) {
            return;
        }
        long used = safe(alloc.usedAmount());
        long allocated = safe(alloc.allocatedAmount());
        if (used + amount > allocated) {
            throw Errors.of(PlatformErrorCode.QUOTA_MEMBER_ALLOC_INSUFFICIENT);
        }
    }

    @Transactional
    public void recordMemberUsage(Long accountId, Long userId, long amount) {
        if (userId == null || amount <= 0) {
            return;
        }
        QuotaMemberAlloc alloc = quotaMemberAllocRepository.findByAccountAndUser(accountId, userId);
        if (alloc == null) {
            return;
        }
        QuotaMemberAlloc updated = new QuotaMemberAlloc(
                alloc.id(), alloc.accountId(), alloc.userId(),
                alloc.allocatedAmount(), safe(alloc.usedAmount()) + amount);
        quotaMemberAllocRepository.updateUsedAmount(updated);
    }

    private long safe(Long value) {
        return value == null ? 0L : value;
    }
}
