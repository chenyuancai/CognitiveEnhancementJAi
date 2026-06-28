package cn.cyc.ai.cog.platform.billing.service;

import cn.cyc.ai.cog.platform.billing.domain.Subscription;
import cn.cyc.ai.cog.platform.billing.domain.SubscriptionPackage;
import cn.cyc.ai.cog.platform.billing.entity.SubscriptionPhase;
import cn.cyc.ai.cog.platform.billing.repository.OrderRepository;
import cn.cyc.ai.cog.platform.billing.repository.SubscriptionPackageRepository;
import cn.cyc.ai.cog.platform.billing.repository.SubscriptionRepository;
import cn.cyc.ai.cog.platform.membership.domain.AccountMembership;
import cn.cyc.ai.cog.platform.membership.repository.AccountMembershipRepository;
import cn.cyc.ai.cog.platform.membership.repository.MembershipLevelRepository;
import cn.cyc.ai.cog.platform.membership.service.MembershipLevelService;
import cn.cyc.ai.cog.platform.membership.support.MembershipBenefitSupport;
import cn.cyc.ai.cog.platform.quota.repository.QuotaAccountRepository;
import cn.cyc.ai.cog.platform.quota.service.QuotaService;
import cn.cyc.ai.cog.platform.system.service.SecurityConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillingLifecycleServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private SubscriptionPackageRepository subscriptionPackageRepository;
    @Mock
    private AccountMembershipRepository accountMembershipRepository;
    @Mock
    private MembershipLevelRepository membershipLevelRepository;
    @Mock
    private MembershipLevelService membershipLevelService;
    @Mock
    private QuotaAccountRepository quotaAccountRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private SecurityConfigService securityConfigService;
    @Mock
    private QuotaService quotaService;
    @Mock
    private MembershipBenefitSupport membershipBenefitSupport;

    @InjectMocks
    private BillingLifecycleService billingLifecycleService;

    @Test
    void shouldAdvanceTrialSubscriptionToFormal() {
        LocalDateTime now = LocalDateTime.now();
        Subscription trial = new Subscription(
                10L, 1L, 100L, 200L, 1L, "PRO", "ACTIVE",
                SubscriptionPhase.TRIAL, now.minusDays(8), now.minusDays(1), false, null);
        SubscriptionPackage pkg = new SubscriptionPackage(
                1L, 1L, "sub.pro", "专业版", "2C", 2L, "MONTH", 1,
                19900L, null, 200000L, 1, "SELF_SERVICE", false, "ON_SALE", null, 7);
        AccountMembership membership = new AccountMembership(1L, 1L, 100L, 2L, "PRO", now.plusDays(1), "SUBSCRIPTION");

        when(subscriptionRepository.listTrialExpiredActive(any())).thenReturn(List.of(trial));
        when(subscriptionRepository.listFormalExpiredActive(any())).thenReturn(List.of());
        when(subscriptionPackageRepository.requireById(1L)).thenReturn(pkg);
        when(accountMembershipRepository.findByAccountId(100L)).thenReturn(membership);
        when(subscriptionRepository.findActiveValidByAccountId(eq(100L), any())).thenReturn(null);

        int count = billingLifecycleService.expireSubscriptions();

        assertEquals(1, count);
        verify(subscriptionRepository).advanceToFormal(eq(10L), any(LocalDateTime.class));
        verify(quotaService).grantCycleQuota(eq(100L), eq(100_000L), eq("trial-convert:10"), eq("试用转正发放"));
    }
}
