package cn.cyc.ai.cog.platform.membership.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.membership.domain.AccountMembership;
import cn.cyc.ai.cog.platform.membership.dto.MemberLevelRequest;
import cn.cyc.ai.cog.platform.membership.dto.MemberPageQuery;
import cn.cyc.ai.cog.platform.membership.repository.AccountMembershipRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final AccountMembershipRepository accountMembershipRepository;

    public MemberService(AccountMembershipRepository accountMembershipRepository) {
        this.accountMembershipRepository = accountMembershipRepository;
    }

    public PageResult<AccountMembership> page(MemberPageQuery query) {
        return accountMembershipRepository.page(query);
    }

    public AccountMembership updateLevel(Long id, MemberLevelRequest request) {
        return accountMembershipRepository.updateLevel(id, request);
    }

    public AccountMembership findByAccountId(Long accountId) {
        return accountMembershipRepository.findByAccountId(accountId);
    }

    public long countByTenant(Long tenantId) {
        return accountMembershipRepository.countByTenant(tenantId);
    }
}
