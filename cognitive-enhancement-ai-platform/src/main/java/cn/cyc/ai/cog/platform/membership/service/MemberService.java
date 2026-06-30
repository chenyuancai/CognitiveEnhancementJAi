package cn.cyc.ai.cog.platform.membership.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.membership.domain.AccountMembership;
import cn.cyc.ai.cog.platform.membership.dto.MemberLevelRequest;
import cn.cyc.ai.cog.platform.membership.dto.MemberPageQuery;
import cn.cyc.ai.cog.platform.membership.repository.AccountMembershipRepository;
import org.springframework.stereotype.Service;

/**
 * Member服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class MemberService {

    /** 账户会员仓储。 */
    private final AccountMembershipRepository accountMembershipRepository;

    /**
     * 创建Member服务。
     *
     * @param accountMembershipRepository 账户会员仓储
     */
    public MemberService(AccountMembershipRepository accountMembershipRepository) {
        this.accountMembershipRepository = accountMembershipRepository;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    public PageResult<AccountMembership> page(MemberPageQuery query) {
        return accountMembershipRepository.page(query);
    }

    /**
     * 更新等级。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 更新结果
     */
    public AccountMembership updateLevel(Long id, MemberLevelRequest request) {
        return accountMembershipRepository.updateLevel(id, request);
    }

    /**
     * 查找人账户ID。
     *
     * @param accountId 账户ID
     * @return 查找结果
     */
    public AccountMembership findByAccountId(Long accountId) {
        return accountMembershipRepository.findByAccountId(accountId);
    }

    /**
     * 执行数量人租户。
     *
     * @param tenantId 租户 ID
     * @return 执行结果
     */
    public long countByTenant(Long tenantId) {
        return accountMembershipRepository.countByTenant(tenantId);
    }
}
