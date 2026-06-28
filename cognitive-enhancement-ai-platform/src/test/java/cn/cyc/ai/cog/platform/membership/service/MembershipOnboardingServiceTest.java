package cn.cyc.ai.cog.platform.membership.service;

import cn.cyc.ai.cog.platform.account.domain.Account;
import cn.cyc.ai.cog.platform.membership.domain.MembershipLevel;
import cn.cyc.ai.cog.platform.membership.repository.AccountMembershipRepository;
import cn.cyc.ai.cog.platform.membership.repository.MembershipLevelRepository;
import cn.cyc.ai.cog.platform.membership.support.MembershipBenefitSupport;
import cn.cyc.ai.cog.platform.billing.service.FreeSubscriptionService;
import cn.cyc.ai.cog.platform.quota.service.QuotaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipOnboardingServiceTest {

    @Mock
    private MembershipLevelRepository membershipLevelRepository;
    @Mock
    private AccountMembershipRepository accountMembershipRepository;
    @Mock
    private MembershipLevelService membershipLevelService;
    @Mock
    private MembershipBenefitSupport membershipBenefitSupport;
    @Mock
    private QuotaService quotaService;
    @Mock
    private FreeSubscriptionService freeSubscriptionService;

    @InjectMocks
    private MembershipOnboardingService membershipOnboardingService;

    @Test
    void shouldGrantFreeBundleWithMonthlyCycleQuota() {
        Account account = new Account(9L, 1L, "INDIVIDUAL", "2C", "Alice", 5L, "ENABLED");
        MembershipLevel free = new MembershipLevel(
                1L, "FREE", "免费版", "2C", true, 10, "ENABLED", "{\"monthlyTokenK\":100}");

        when(membershipLevelRepository.requireDefaultForSegment("2C")).thenReturn(free);
        when(accountMembershipRepository.findByAccountId(9L)).thenReturn(null);
        when(membershipBenefitSupport.resolveMonthlyTokenQuota(1L, free.benefitsJson())).thenReturn(100_000L);

        membershipOnboardingService.grantFreeBundle(account, 5L);

        verify(accountMembershipRepository).grantInitial(1L, 9L, 1L, "FREE", "DEFAULT");
        verify(membershipLevelService).writeChangeLog(9L, null, "FREE", "REGISTER", "用户注册开户");
        verify(freeSubscriptionService).ensureFreeSubscription(account, free, null);
        verify(quotaService).initFreeMonthlyCycle(
                eq(1L),
                eq(9L),
                eq(100_000L),
                any(LocalDateTime.class),
                eq("onboarding:free:9"),
                eq("注册开通 Free 月额度"));
    }
}
