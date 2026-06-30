package cn.cyc.ai.cog.platform.account.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.account.domain.Account;
import cn.cyc.ai.cog.platform.account.dto.UserMeContext;
import cn.cyc.ai.cog.platform.account.repository.AccountRepository;
import cn.cyc.ai.cog.platform.iam.domain.IamUser;
import cn.cyc.ai.cog.platform.iam.repository.IamUserRepository;
import cn.cyc.ai.cog.platform.membership.domain.AccountMembership;
import cn.cyc.ai.cog.platform.membership.repository.AccountMembershipRepository;
import cn.cyc.ai.cog.platform.org.domain.Organization;
import cn.cyc.ai.cog.platform.org.repository.OrganizationRepository;
import cn.cyc.ai.cog.platform.quota.domain.QuotaAccount;
import cn.cyc.ai.cog.platform.quota.repository.QuotaAccountRepository;
import cn.cyc.ai.cog.api.enums.IamUserStatus;
import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.ServiceException;
import org.springframework.stereotype.Service;

/**
 * 当前用户商业上下文聚合服务（用户、账户、组织、会员、额度）。
 * <p>
 * 供 C 端 {@code /api/app/auth/me} 与 Admin {@code /api/admin/auth/me} 共用账户域数据，
 * RBAC/菜单由 Admin 侧 {@link cn.cyc.ai.cog.admin.auth.service.AuthMeService} 额外补充。
 * </p>
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class UserMeContextService {

    /** IAM 用户仓储 */
    private final IamUserRepository iamUserRepository;

    /** 商业账户仓储 */
    private final AccountRepository accountRepository;

    /** 账户会员关系仓储 */
    private final AccountMembershipRepository accountMembershipRepository;

    /** 额度账户仓储 */
    private final QuotaAccountRepository quotaAccountRepository;

    /** 组织仓储 */
    private final OrganizationRepository organizationRepository;

    /**
     * @param iamUserRepository             IAM 用户仓储
     * @param accountRepository             账户仓储
     * @param accountMembershipRepository   会员关系仓储
     * @param quotaAccountRepository        额度账户仓储
     * @param organizationRepository        组织仓储
     */
    public UserMeContextService(IamUserRepository iamUserRepository,
                                AccountRepository accountRepository,
                                AccountMembershipRepository accountMembershipRepository,
                                QuotaAccountRepository quotaAccountRepository,
                                OrganizationRepository organizationRepository) {
        this.iamUserRepository = iamUserRepository;
        this.accountRepository = accountRepository;
        this.accountMembershipRepository = accountMembershipRepository;
        this.quotaAccountRepository = quotaAccountRepository;
        this.organizationRepository = organizationRepository;
    }

    /**
     * 基于当前请求 {@link UserContext} 构建用户商业上下文。
     *
     * @return 用户商业上下文快照
     */
    public UserMeContext buildForCurrentUser() {
        AuthUser authUser = UserContext.get();
        if (authUser == null || authUser.getUserId() == null) {
            throw Errors.of(PlatformErrorCode.UNAUTHORIZED);
        }
        return buildForUserId(authUser.getUserId());
    }

    /**
     * 基于指定用户 ID 构建商业上下文。
     *
     * @param userId 用户 ID
     * @return 用户商业上下文快照
     */
    public UserMeContext buildForUserId(Long userId) {
        IamUser user = iamUserRepository.resolveBanIfExpired(userId);
        if (!IamUserStatus.ENABLED.matches(user.status())) {
            throw Errors.of(PlatformErrorCode.USER_STATUS_UNAVAILABLE, "用户状态不可用：" + user.status());
        }
        Account account = resolveAccount(user);
        AccountMembership membership = accountMembershipRepository.findByAccountId(account.id());
        QuotaAccount quota = quotaAccountRepository.findByAccountId(account.id());

        UserMeContext context = new UserMeContext();
        context.setUser(toUser(user));
        context.setAccount(toAccount(account));
        context.setOrganization(toOrganization(account.id()));
        context.setSegment(account.segment());
        context.setMembership(toMembership(membership));
        context.setQuota(toQuota(quota));
        return context;
    }

    /**
     * 执行resolve账户。
     *
     * @param user 用户
     * @return 执行结果
     */
    private Account resolveAccount(IamUser user) {
        if (user.primaryAccountId() != null) {
            Account account = accountRepository.findById(user.primaryAccountId());
            if (account != null) {
                return account;
            }
        }
        Account account = accountRepository.findByOwnerUserId(user.id());
        if (account == null) {
            throw Errors.of(PlatformErrorCode.USER_ACCOUNT_NOT_BOUND);
        }
        return account;
    }

    /**
     * 转换为用户。
     *
     * @param user 用户
     * @return 转换结果
     */
    private UserMeContext.UserSnapshot toUser(IamUser user) {
        UserMeContext.UserSnapshot snapshot = new UserMeContext.UserSnapshot();
        snapshot.setId(user.id());
        snapshot.setUsername(user.username());
        snapshot.setNickname(user.nickname());
        snapshot.setEmail(user.email());
        snapshot.setAvatarUrl(user.avatarUrl());
        snapshot.setStatus(user.status());
        return snapshot;
    }

    /**
     * 转换为账户。
     *
     * @param account 账户
     * @return 转换结果
     */
    private UserMeContext.AccountSnapshot toAccount(Account account) {
        UserMeContext.AccountSnapshot snapshot = new UserMeContext.AccountSnapshot();
        snapshot.setId(account.id());
        snapshot.setAccountType(account.accountType());
        snapshot.setSegment(account.segment());
        snapshot.setDisplayName(account.displayName());
        return snapshot;
    }

    /**
     * 转换为Organization。
     *
     * @param accountId 账户ID
     * @return 转换结果
     */
    private UserMeContext.OrganizationSnapshot toOrganization(Long accountId) {
        Organization org = organizationRepository.findByAccountId(accountId);
        if (org == null) {
            return null;
        }
        UserMeContext.OrganizationSnapshot snapshot = new UserMeContext.OrganizationSnapshot();
        snapshot.setId(org.id());
        snapshot.setOrgName(org.orgName());
        snapshot.setOrgType(org.orgType());
        return snapshot;
    }

    /**
     * 转换为会员。
     *
     * @param membership 会员
     * @return 转换结果
     */
    private UserMeContext.MembershipSnapshot toMembership(AccountMembership membership) {
        if (membership == null) {
            return null;
        }
        UserMeContext.MembershipSnapshot snapshot = new UserMeContext.MembershipSnapshot();
        snapshot.setLevelCode(membership.levelCode());
        snapshot.setLevelName(membership.levelCode());
        snapshot.setExpireAt(membership.expireAt());
        return snapshot;
    }

    /**
     * 转换为额度。
     *
     * @param quota 额度
     * @return 转换结果
     */
    private UserMeContext.QuotaSnapshot toQuota(QuotaAccount quota) {
        if (quota == null) {
            return null;
        }
        UserMeContext.QuotaSnapshot snapshot = new UserMeContext.QuotaSnapshot();
        snapshot.setCycleRemaining(quota.cycleRemaining());
        snapshot.setGiftRemaining(quota.giftRemaining());
        snapshot.setTopupRemaining(quota.topupRemaining());
        return snapshot;
    }
}
