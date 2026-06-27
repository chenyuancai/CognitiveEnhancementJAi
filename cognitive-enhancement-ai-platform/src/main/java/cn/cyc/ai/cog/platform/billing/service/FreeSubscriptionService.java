package cn.cyc.ai.cog.platform.billing.service;

import cn.cyc.ai.cog.platform.account.domain.Account;
import cn.cyc.ai.cog.platform.billing.domain.Subscription;
import cn.cyc.ai.cog.platform.billing.domain.SubscriptionPackage;
import cn.cyc.ai.cog.platform.billing.entity.SubscriptionPhase;
import cn.cyc.ai.cog.platform.billing.repository.SubscriptionPackageRepository;
import cn.cyc.ai.cog.platform.billing.repository.SubscriptionRepository;
import cn.cyc.ai.cog.platform.membership.domain.MembershipLevel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 免费会员订阅记录：注册赠送或手动授予 FREE 时写入 {@code qz_bill_subscription}。
 */
@Service
public class FreeSubscriptionService {

    /** 注册赠送免费套餐编码（见 Flyway V36）。 */
    public static final String FREE_ONBOARDING_PACKAGE_CODE = "sub.free.default";

    /** FREE 默认订阅到期时间（永久有效，避免被过期任务误降级）。 */
    public static final LocalDateTime FREE_PERPETUAL_END = LocalDateTime.of(2099, 12, 31, 23, 59, 59);

    private static final String FREE_LEVEL_CODE = "FREE";

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPackageRepository subscriptionPackageRepository;

    public FreeSubscriptionService(SubscriptionRepository subscriptionRepository,
                                   SubscriptionPackageRepository subscriptionPackageRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPackageRepository = subscriptionPackageRepository;
    }

    /**
     * 确保账户存在有效的 FREE 订阅记录（幂等：已有任意有效订阅则跳过）。
     *
     * @param account 商业账户
     * @param level   会员等级（须为 FREE）
     * @param endAt   订阅到期时间；为空则使用永久截止日
     */
    public void ensureFreeSubscription(Account account, MembershipLevel level, LocalDateTime endAt) {
        if (level == null || !FREE_LEVEL_CODE.equalsIgnoreCase(level.levelCode())) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        Subscription active = subscriptionRepository.findActiveValidByAccountId(account.id(), now);
        if (active != null) {
            return;
        }
        SubscriptionPackage pkg = subscriptionPackageRepository.findByPackageCode(FREE_ONBOARDING_PACKAGE_CODE);
        Long packageId = pkg == null ? null : pkg.id();
        LocalDateTime subscriptionEnd = endAt != null ? endAt : FREE_PERPETUAL_END;
        subscriptionRepository.insertActive(
                account.tenantId(),
                account.id(),
                null,
                packageId,
                level.levelCode(),
                SubscriptionPhase.FORMAL,
                now,
                subscriptionEnd);
    }
}
