package cn.cyc.ai.cog.platform.billing.service;

import cn.cyc.ai.cog.platform.account.domain.Account;
import cn.cyc.ai.cog.platform.billing.domain.Subscription;
import cn.cyc.ai.cog.platform.billing.domain.SubscriptionPackage;
import cn.cyc.ai.cog.platform.billing.entity.SubscriptionPhase;
import cn.cyc.ai.cog.platform.billing.repository.SubscriptionPackageRepository;
import cn.cyc.ai.cog.platform.billing.repository.SubscriptionRepository;
import cn.cyc.ai.cog.platform.membership.domain.MembershipLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreeSubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private SubscriptionPackageRepository subscriptionPackageRepository;

    @InjectMocks
    private FreeSubscriptionService freeSubscriptionService;

    @Test
    void shouldInsertFreeSubscriptionWhenNoActiveSubscription() {
        Account account = new Account(9L, 1L, "INDIVIDUAL", "2C", "Alice", 5L, "ENABLED");
        MembershipLevel free = new MembershipLevel(
                1L, "FREE", "免费版", "2C", true, 10, "ENABLED", "{}");
        SubscriptionPackage pkg = new SubscriptionPackage(
                100L, 1L, FreeSubscriptionService.FREE_ONBOARDING_PACKAGE_CODE, "免费版",
                "2C", 1L, "MONTH", 1, 0L, null, 100_000L, 1, "GIFT", false, "OFF_SALE", null, 0);

        when(subscriptionRepository.findActiveValidByAccountId(eq(9L), any(LocalDateTime.class))).thenReturn(null);
        when(subscriptionPackageRepository.findByPackageCode(FreeSubscriptionService.FREE_ONBOARDING_PACKAGE_CODE))
                .thenReturn(pkg);

        freeSubscriptionService.ensureFreeSubscription(account, free, null);

        verify(subscriptionRepository).insertActive(
                eq(1L),
                eq(9L),
                isNull(),
                eq(100L),
                eq("FREE"),
                eq(SubscriptionPhase.FORMAL),
                any(LocalDateTime.class),
                eq(FreeSubscriptionService.FREE_PERPETUAL_END));
    }

    @Test
    void shouldSkipWhenActiveSubscriptionExists() {
        Account account = new Account(9L, 1L, "INDIVIDUAL", "2C", "Alice", 5L, "ENABLED");
        MembershipLevel free = new MembershipLevel(
                1L, "FREE", "免费版", "2C", true, 10, "ENABLED", "{}");
        when(subscriptionRepository.findActiveValidByAccountId(eq(9L), any(LocalDateTime.class)))
                .thenReturn(new Subscription(1L, 1L, 9L, 2L, 100L, "PRO", "ACTIVE",
                        SubscriptionPhase.FORMAL, LocalDateTime.now(), LocalDateTime.now().plusMonths(1),
                        false, null));

        freeSubscriptionService.ensureFreeSubscription(account, free, null);

        verify(subscriptionRepository, never()).insertActive(
                any(), any(), any(), any(), any(), any(), any(), any());
    }
}
