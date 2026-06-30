package cn.cyc.ai.cog.platform.membership.service;

import cn.cyc.ai.cog.platform.membership.domain.AccountMembership;
import cn.cyc.ai.cog.platform.membership.repository.AccountMembershipRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员域只读统计服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class MembershipStatsService {

    /** 账户会员关系仓储 */
    private final AccountMembershipRepository accountMembershipRepository;

    /**
     * @param accountMembershipRepository 账户会员关系仓储
     */
    public MembershipStatsService(AccountMembershipRepository accountMembershipRepository) {
        this.accountMembershipRepository = accountMembershipRepository;
    }

    /**
     * 统计付费会员数。
     *
     * @param tenantId 租户 ID
     * @return 付费会员数
     */
    public long countPaidMembers(Long tenantId) {
        return accountMembershipRepository.countPaidMembers(tenantId);
    }

    /**
     * 会员等级分布。
     *
     * @param tenantId 租户 ID
     * @return 等级编码 → 数量
     */
    public Map<String, Long> levelDistribution(Long tenantId) {
        Map<String, Long> grouped = new LinkedHashMap<>();
        for (AccountMembership membership : accountMembershipRepository.listByTenant(tenantId)) {
            String level = membership.levelCode() == null ? "UNKNOWN" : membership.levelCode();
            grouped.merge(level, 1L, Long::sum);
        }
        return grouped;
    }

    /**
     * 统计即将到期会员数。
     *
     * @param tenantId 租户 ID
     * @param days     向前展望天数
     * @return 即将到期会员数
     */
    public long countExpiring(Long tenantId, int days) {
        return accountMembershipRepository.countExpiringWithin(tenantId, days);
    }
}
