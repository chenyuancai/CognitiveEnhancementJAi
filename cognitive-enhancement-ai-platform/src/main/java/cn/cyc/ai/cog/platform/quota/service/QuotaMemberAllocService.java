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

/**
 * 额度MemberAlloc服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class QuotaMemberAllocService {

    /** 额度MemberAlloc仓储。 */
    private final QuotaMemberAllocRepository quotaMemberAllocRepository;

    /**
     * 创建额度MemberAlloc服务。
     *
     * @param quotaMemberAllocRepository 额度MemberAlloc仓储
     */
    public QuotaMemberAllocService(QuotaMemberAllocRepository quotaMemberAllocRepository) {
        this.quotaMemberAllocRepository = quotaMemberAllocRepository;
    }

    /**
     * 查询人账户列表。
     *
     * @param accountId 账户ID
     * @return 结果列表
     */
    public List<QuotaMemberAlloc> listByAccount(Long accountId) {
        return quotaMemberAllocRepository.listByAccount(accountId);
    }

    /**
     * 获取人账户And用户。
     *
     * @param accountId 账户ID
     * @param userId 用户 ID
     * @return 人账户And用户
     */
    public QuotaMemberAlloc getByAccountAndUser(Long accountId, Long userId) {
        return quotaMemberAllocRepository.findByAccountAndUser(accountId, userId);
    }

    /**
     * 执行allocate。
     *
     * @param accountId 账户ID
     * @param request 请求
     * @return 执行结果
     */
    @Transactional
    public QuotaMemberAlloc allocate(Long accountId, QuotaMemberAllocSaveRequest request) {
        return quotaMemberAllocRepository.allocate(accountId, request);
    }

    /**
     * 删除Item。
     *
     * @param accountId 账户ID
     * @param userId 用户 ID
     */
    @Transactional
    public void remove(Long accountId, Long userId) {
        quotaMemberAllocRepository.remove(accountId, userId);
    }

    /**
     * 执行assertMember额度。
     *
     * @param accountId 账户ID
     * @param userId 用户 ID
     * @param amount amount
     */
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

    /**
     * 执行recordMemberUsage。
     *
     * @param accountId 账户ID
     * @param userId 用户 ID
     * @param amount amount
     */
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

    /**
     * 执行safe。
     *
     * @param value 值
     * @return 执行结果
     */
    private long safe(Long value) {
        return value == null ? 0L : value;
    }
}
