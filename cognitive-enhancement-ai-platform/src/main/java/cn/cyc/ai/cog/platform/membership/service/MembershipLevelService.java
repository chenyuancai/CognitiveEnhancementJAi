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

/**
 * 会员等级服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class MembershipLevelService {

    /** 会员等级仓储。 */
    private final MembershipLevelRepository membershipLevelRepository;
    /** 账户会员仓储。 */
    private final AccountMembershipRepository accountMembershipRepository;
    /** 会员ChangeLog仓储。 */
    private final MembershipChangeLogRepository membershipChangeLogRepository;
    /** 账户仓储。 */
    private final AccountRepository accountRepository;
    /** freeSubscription服务。 */
    private final FreeSubscriptionService freeSubscriptionService;

    /**
     * 创建会员等级服务。
     */
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

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    public PageResult<MembershipLevel> page(MembershipLevelPageQuery query) {
        return membershipLevelRepository.page(query);
    }

    /**
     * 查询All列表。
     *
     * @param segment segment
     * @return 结果列表
     */
    public List<MembershipLevel> listAll(String segment) {
        return membershipLevelRepository.listEnabled(segment);
    }

    /**
     * 执行detail。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    public MembershipLevel detail(Long id) {
        return membershipLevelRepository.findById(id);
    }

    /**
     * 查找人编码。
     *
     * @param levelCode 等级编码
     * @return 查找结果
     */
    public MembershipLevel findByCode(String levelCode) {
        return membershipLevelRepository.findByCode(levelCode);
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    public MembershipLevel create(MembershipLevelSaveRequest request) {
        return membershipLevelRepository.create(request);
    }

    /**
     * 更新Item。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 更新结果
     */
    public MembershipLevel update(Long id, MembershipLevelSaveRequest request) {
        return membershipLevelRepository.update(id, request);
    }

    /**
     * 执行grant。
     *
     * @param request 请求
     * @return 执行结果
     */
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

    /**
     * 执行分页ChangeLogs。
     *
     * @param current current
     * @param size 大小
     * @param accountId 账户ID
     * @return 执行结果
     */
    public PageResult<MembershipChangeLog> pageChangeLogs(long current, long size, Long accountId) {
        return membershipChangeLogRepository.page(current, size, accountId);
    }

    /**
     * 执行writeChangeLog。
     *
     * @param accountId 账户ID
     * @param fromLevel from等级
     * @param toLevel to等级
     * @param changeType change类型
     * @param remark remark
     */
    public void writeChangeLog(Long accountId, String fromLevel, String toLevel, String changeType, String remark) {
        membershipChangeLogRepository.insert(accountId, fromLevel, toLevel, changeType, remark);
    }
}
