package cn.cyc.ai.cog.platform.membership.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.billing.service.FreeSubscriptionService;
import cn.cyc.ai.cog.platform.membership.domain.AccountMembership;
import cn.cyc.ai.cog.platform.membership.domain.MembershipChangeLog;
import cn.cyc.ai.cog.platform.membership.domain.MembershipLevel;
import cn.cyc.ai.cog.platform.membership.dto.GrantMembershipRequest;
import cn.cyc.ai.cog.platform.membership.dto.MembershipLevelPageQuery;
import cn.cyc.ai.cog.platform.membership.dto.MembershipLevelSaveRequest;
import cn.cyc.ai.cog.platform.membership.repository.AccountMembershipRepository;
import cn.cyc.ai.cog.platform.membership.repository.MembershipChangeLogRepository;
import cn.cyc.ai.cog.platform.account.domain.Account;
import cn.cyc.ai.cog.platform.account.repository.AccountRepository;
import cn.cyc.ai.cog.platform.billing.service.FreeSubscriptionService;
import cn.cyc.ai.cog.platform.membership.repository.MembershipLevelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MembershipLevelService {

    private final MembershipLevelRepository membershipLevelRepository;
    private final AccountMembershipRepository accountMembershipRepository;
    private final MembershipChangeLogRepository membershipChangeLogRepository;
    private final AccountRepository accountRepository;
    private final FreeSubscriptionService freeSubscriptionService;

    public MembershipLevelService(MembershipLevelRepository membershipLevelRepository,
                                  AccountMembershipRepository accountMembershipRepository,
                                  MembershipChangeLogRepository membershipChangeLogRepository,
                                  AccountRepository accountRepository,
                                  FreeSubscriptionService freeSubscriptionService) {
        this.membershipLevelRepository = membershipLevelRepository;
        this.accountMembershipRepository = accountMembershipRepository;
        this.membershipChangeLogRepository = membershipChangeLogRepository;
        this.accountRepository = accountRepository;
        this.freeSubscriptionService = freeSubscriptionService;
    }

    public PageResult<MembershipLevel> page(MembershipLevelPageQuery query) {
        return membershipLevelRepository.page(query);
    }

    public List<MembershipLevel> listAll(String segment) {
        return membershipLevelRepository.listEnabled(segment);
    }

    public MembershipLevel detail(Long id) {
        return membershipLevelRepository.findById(id);
    }

    public MembershipLevel findByCode(String levelCode) {
        return membershipLevelRepository.findByCode(levelCode);
    }

    public MembershipLevel create(MembershipLevelSaveRequest request) {
        return membershipLevelRepository.create(request);
    }

    public MembershipLevel update(Long id, MembershipLevelSaveRequest request) {
        return membershipLevelRepository.update(id, request);
    }

    @Transactional
    public AccountMembership grant(GrantMembershipRequest request) {
        MembershipLevel level = membershipLevelRepository.findById(request.getLevelId());
        AccountMembership existing = accountMembershipRepository.findByAccountId(request.getAccountId());
        String fromLevel = existing == null ? null : existing.levelCode();
        AccountMembership membership = accountMembershipRepository.upsertGrant(
                request.getAccountId(), level.id(), level.levelCode(), request.getExpireAt(), "GRANT");
        writeChangeLog(request.getAccountId(), fromLevel, level.levelCode(), "MANUAL", request.getRemark());
        Account account = accountRepository.requireById(request.getAccountId());
        freeSubscriptionService.ensureFreeSubscription(account, level, request.getExpireAt());
        return membership;
    }

    public PageResult<MembershipChangeLog> pageChangeLogs(long current, long size, Long accountId) {
        return membershipChangeLogRepository.page(current, size, accountId);
    }

    public void writeChangeLog(Long accountId, String fromLevel, String toLevel, String changeType, String remark) {
        membershipChangeLogRepository.insert(accountId, fromLevel, toLevel, changeType, remark);
    }
}
