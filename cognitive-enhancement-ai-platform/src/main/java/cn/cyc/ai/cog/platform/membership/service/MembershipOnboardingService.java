package cn.cyc.ai.cog.platform.membership.service;

import cn.cyc.ai.cog.platform.account.domain.Account;
import cn.cyc.ai.cog.platform.billing.service.FreeSubscriptionService;
import cn.cyc.ai.cog.platform.membership.domain.AccountMembership;
import cn.cyc.ai.cog.platform.membership.domain.MembershipLevel;
import cn.cyc.ai.cog.platform.membership.repository.AccountMembershipRepository;
import cn.cyc.ai.cog.platform.membership.repository.MembershipLevelRepository;
import cn.cyc.ai.cog.platform.membership.support.MembershipBenefitSupport;
import cn.cyc.ai.cog.platform.quota.service.QuotaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * C 端开户礼包：FREE 会员身份 + 首月 cycle 额度。
 */
@Service
public class MembershipOnboardingService {

    private static final String SEGMENT_2C = "2C";
    private static final String ONBOARDING_IDEM_PREFIX = "onboarding:free:";

    private final MembershipLevelRepository membershipLevelRepository;
    private final AccountMembershipRepository accountMembershipRepository;
    private final MembershipLevelService membershipLevelService;
    private final MembershipBenefitSupport membershipBenefitSupport;
    private final QuotaService quotaService;
    private final FreeSubscriptionService freeSubscriptionService;

    public MembershipOnboardingService(MembershipLevelRepository membershipLevelRepository,
                                       AccountMembershipRepository accountMembershipRepository,
                                       MembershipLevelService membershipLevelService,
                                       MembershipBenefitSupport membershipBenefitSupport,
                                       QuotaService quotaService,
                                       FreeSubscriptionService freeSubscriptionService) {
        this.membershipLevelRepository = membershipLevelRepository;
        this.accountMembershipRepository = accountMembershipRepository;
        this.membershipLevelService = membershipLevelService;
        this.membershipBenefitSupport = membershipBenefitSupport;
        this.quotaService = quotaService;
        this.freeSubscriptionService = freeSubscriptionService;
    }

    /**
     * 发放 Free 会员包（仅月度 cycle 额度）。幂等：同一 accountId 不重复写额度流水。
     */
    @Transactional
    public void grantFreeBundle(Account account, Long userId) {
        MembershipLevel freeLevel = membershipLevelRepository.requireDefaultForSegment(SEGMENT_2C);
        AccountMembership existing = accountMembershipRepository.findByAccountId(account.id());
        if (existing == null) {
            accountMembershipRepository.grantInitial(
                    account.tenantId(),
                    account.id(),
                    freeLevel.id(),
                    freeLevel.levelCode(),
                    "DEFAULT");
            membershipLevelService.writeChangeLog(
                    account.id(), null, freeLevel.levelCode(), "REGISTER", "用户注册开户");
        }

        freeSubscriptionService.ensureFreeSubscription(account, freeLevel, null);

        long monthlyTokens = membershipBenefitSupport.resolveMonthlyTokenQuota(
                freeLevel.id(), freeLevel.benefitsJson());
        LocalDateTime cycleResetAt = nextMonthStart(LocalDate.now());
        String idempotencyKey = ONBOARDING_IDEM_PREFIX + account.id();
        quotaService.initFreeMonthlyCycle(
                account.tenantId(),
                account.id(),
                monthlyTokens,
                cycleResetAt,
                idempotencyKey,
                "注册开通 Free 月额度");
    }

    public static LocalDateTime nextMonthStart(LocalDate today) {
        return today.withDayOfMonth(1).plusMonths(1).atStartOfDay();
    }
}
