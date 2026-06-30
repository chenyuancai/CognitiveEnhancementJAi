package cn.cyc.ai.cog.platform.account.service;

import cn.cyc.ai.cog.platform.account.domain.Account;
import cn.cyc.ai.cog.platform.account.repository.AccountRepository;
import cn.cyc.ai.cog.platform.iam.domain.Tenant;
import cn.cyc.ai.cog.platform.iam.repository.IamUserRepository;
import cn.cyc.ai.cog.platform.iam.repository.TenantRepository;
import cn.cyc.ai.cog.platform.membership.domain.MembershipLevel;
import cn.cyc.ai.cog.platform.membership.repository.AccountMembershipRepository;
import cn.cyc.ai.cog.platform.membership.repository.MembershipLevelRepository;
import cn.cyc.ai.cog.platform.membership.service.MembershipOnboardingService;
import cn.cyc.ai.cog.platform.membership.support.MembershipBenefitSupport;
import cn.cyc.ai.cog.platform.org.domain.Organization;
import cn.cyc.ai.cog.platform.org.dto.CreateOrganizationRequest;
import cn.cyc.ai.cog.platform.org.repository.OrgMemberRepository;
import cn.cyc.ai.cog.platform.org.repository.OrganizationRepository;
import cn.cyc.ai.cog.platform.quota.repository.QuotaAccountRepository;
import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.common.spi.AccountProvisioner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.UUID;

/**
 * 商业账户编排：2C 个人开户、2B/2G 组织开户。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AccountService implements AccountProvisioner {

    /** 账户INDIVIDUAL。 */
    private static final String ACCOUNT_INDIVIDUAL = "INDIVIDUAL";
    /** 账户ENTERPRISE。 */
    private static final String ACCOUNT_ENTERPRISE = "ENTERPRISE";
    /** 账户GOVERNMENT。 */
    private static final String ACCOUNT_GOVERNMENT = "GOVERNMENT";
    /** SEGMENT2C。 */
    private static final String SEGMENT_2C = "2C";
    /** SEGMENT2B。 */
    private static final String SEGMENT_2B = "2B";
    /** SEGMENT2G。 */
    private static final String SEGMENT_2G = "2G";
    /** ORG默认CYCLEMULTIPLIER。 */
    private static final long ORG_DEFAULT_CYCLE_MULTIPLIER = 10L;

    /** 商业账户仓储 */
    private final AccountRepository accountRepository;

    /** 账户会员关系仓储 */
    private final AccountMembershipRepository accountMembershipRepository;

    /** 额度账户仓储 */
    private final QuotaAccountRepository quotaAccountRepository;

    /** IAM 用户仓储 */
    private final IamUserRepository iamUserRepository;

    /** 租户仓储 */
    private final TenantRepository tenantRepository;

    /** 组织仓储 */
    private final OrganizationRepository organizationRepository;

    /** 组织成员仓储 */
    private final OrgMemberRepository orgMemberRepository;

    /** 会员等级仓储 */
    private final MembershipLevelRepository membershipLevelRepository;

    /** C 端开户礼包 */
    private final MembershipOnboardingService membershipOnboardingService;

    /** 会员权益解析 */
    private final MembershipBenefitSupport membershipBenefitSupport;

    /**
     * @param accountRepository             商业账户仓储
     * @param accountMembershipRepository   账户会员关系仓储
     * @param quotaAccountRepository        额度账户仓储
     * @param iamUserRepository             IAM 用户仓储
     * @param tenantRepository              租户仓储
     * @param organizationRepository        组织仓储
     * @param orgMemberRepository           组织成员仓储
     * @param membershipLevelRepository     会员等级仓储
     * @param membershipOnboardingService   开户礼包服务
     * @param membershipBenefitSupport      会员权益解析
     */
    public AccountService(AccountRepository accountRepository,
                          AccountMembershipRepository accountMembershipRepository,
                          QuotaAccountRepository quotaAccountRepository,
                          IamUserRepository iamUserRepository,
                          TenantRepository tenantRepository,
                          OrganizationRepository organizationRepository,
                          OrgMemberRepository orgMemberRepository,
                          MembershipLevelRepository membershipLevelRepository,
                          MembershipOnboardingService membershipOnboardingService,
                          MembershipBenefitSupport membershipBenefitSupport) {
        this.accountRepository = accountRepository;
        this.accountMembershipRepository = accountMembershipRepository;
        this.quotaAccountRepository = quotaAccountRepository;
        this.iamUserRepository = iamUserRepository;
        this.tenantRepository = tenantRepository;
        this.organizationRepository = organizationRepository;
        this.orgMemberRepository = orgMemberRepository;
        this.membershipLevelRepository = membershipLevelRepository;
        this.membershipOnboardingService = membershipOnboardingService;
        this.membershipBenefitSupport = membershipBenefitSupport;
    }

    /**
     * 为个人用户开户（2C）：幂等，已存在则绑定主账户后返回。
     *
     * @param userId      用户 ID
     * @param displayName 展示名称
     * @return 商业账户 ID
     */
    @Override
    @Transactional
    public Long provisionIndividual(Long userId, String displayName) {
        Account existing = accountRepository.findIndividualByOwnerUserId(userId);
        if (existing != null) {
            iamUserRepository.bindPrimaryAccountIfAbsent(userId, existing.id());
            return existing.id();
        }
        String name = StringUtils.hasText(displayName) ? displayName : "用户" + userId;
        Account account = accountRepository.insert(new Account(
                null,
                CommonConstants.DEFAULT_TENANT_ID,
                ACCOUNT_INDIVIDUAL,
                SEGMENT_2C,
                name,
                userId,
                CommonConstants.STATUS_ENABLED
        ));

        membershipOnboardingService.grantFreeBundle(account, userId);
        iamUserRepository.bindPrimaryAccountIfAbsent(userId, account.id());
        return account.id();
    }

    /**
     * 2B/2G 组织开户：独立租户 + 组织 + 账户 + 默认会员额度。
     *
     * @param request 组织创建请求
     * @return 持久化后的组织
     */
    @Transactional
    public Organization createOrganization(CreateOrganizationRequest request) {
        iamUserRepository.requireById(request.getOwnerUserId());
        String segment = normalizeSegment(request.getSegment());
        String orgType = SEGMENT_2G.equals(segment) ? ACCOUNT_GOVERNMENT : ACCOUNT_ENTERPRISE;

        Tenant tenant = tenantRepository.insertForOrganization(
                generateTenantCode(request.getOrgName()),
                request.getOrgName(),
                segment);

        Account account = accountRepository.insert(new Account(
                null,
                tenant.id(),
                orgType,
                segment,
                request.getOrgName(),
                request.getOwnerUserId(),
                CommonConstants.STATUS_ENABLED
        ));

        Organization organization = organizationRepository.insert(new Organization(
                null,
                tenant.id(),
                account.id(),
                orgType,
                request.getOrgName(),
                request.getUnifiedSocialCode(),
                request.getSeatLimit() == null ? 10 : request.getSeatLimit(),
                request.getContactName(),
                request.getContactPhone(),
                null,
                null
        ));

        orgMemberRepository.insertOwner(tenant.id(), organization.id(), request.getOwnerUserId());

        MembershipLevel defaultLevel = membershipLevelRepository.requireDefaultForSegment(segment);
        initMembership(account, defaultLevel);
        long orgCycleQuota = membershipBenefitSupport.resolveMonthlyTokenQuota(
                defaultLevel.id(), defaultLevel.benefitsJson()) * ORG_DEFAULT_CYCLE_MULTIPLIER;
        quotaAccountRepository.insertInitial(account.tenantId(), account.id(), orgCycleQuota);

        iamUserRepository.updateTenantAndPrimaryAccount(request.getOwnerUserId(), tenant.id(), account.id());
        return organization;
    }

    /**
     * 按 ID 查询商业账户。
     *
     * @param accountId 账户 ID
     * @return 账户领域对象
     */
    public Account getById(Long accountId) {
        return accountRepository.requireById(accountId);
    }

    /**
     * 执行init会员。
     *
     * @param account 账户
     * @param level 等级
     */
    private void initMembership(Account account, MembershipLevel level) {
        accountMembershipRepository.grantInitial(
                account.tenantId(),
                account.id(),
                level.id(),
                level.levelCode(),
                "DEFAULT");
    }

    /**
     * 执行normalizeSegment。
     *
     * @param segment segment
     * @return 执行结果
     */
    private String normalizeSegment(String segment) {
        if (!StringUtils.hasText(segment)) {
            return SEGMENT_2B;
        }
        return segment.toUpperCase(Locale.ROOT);
    }

    /**
     * 执行generate租户编码。
     *
     * @param orgName org名称
     * @return 执行结果
     */
    private String generateTenantCode(String orgName) {
        String slug = orgName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);
        if (!StringUtils.hasText(slug)) {
            slug = "org";
        }
        if (slug.length() > 20) {
            slug = slug.substring(0, 20);
        }
        return slug + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
