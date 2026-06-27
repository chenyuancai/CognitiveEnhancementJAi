package cn.cyc.ai.cog.platform.billing.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.billing.domain.Subscription;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订阅记录仓储接口。
 */
public interface SubscriptionRepository {

    PageResult<Subscription> page(long current, long size, Long accountId);

    Subscription findByOrderId(Long orderId);

    Subscription insertActive(Long tenantId, Long accountId, Long orderId, Long packageId,
                            String levelCode, String phase, LocalDateTime startAt, LocalDateTime endAt);

    void markRefunded(Long subscriptionId);

    /**
     * 查询试用期已到期但仍为 ACTIVE 的订阅。
     *
     * @param now 当前时间
     * @return 试用到期订阅列表
     */
    List<Subscription> listTrialExpiredActive(LocalDateTime now);

    /**
     * 查询正式周期已到期但仍为 ACTIVE 的订阅。
     *
     * @param now 当前时间
     * @return 正式到期订阅列表
     */
    List<Subscription> listFormalExpiredActive(LocalDateTime now);

    /**
     * 将订阅推进为正式周期。
     *
     * @param subscriptionId 订阅 ID
     * @param endAt          新的到期时间
     */
    void advanceToFormal(Long subscriptionId, LocalDateTime endAt);

    /**
     * 查询已过期但仍为 ACTIVE 的订阅（兼容旧逻辑，仅正式段）。
     *
     * @param now 当前时间
     * @return 过期订阅列表
     * @deprecated 使用 {@link #listFormalExpiredActive(LocalDateTime)}
     */
    @Deprecated
    List<Subscription> listExpiredActive(LocalDateTime now);

    /**
     * 将订阅标记为 EXPIRED。
     *
     * @param subscriptionId 订阅 ID
     */
    void markExpired(Long subscriptionId);

    /**
     * 查询账户当前有效订阅（ACTIVE 且未过期），不存在返回 null。
     *
     * @param accountId 账户 ID
     * @param now       当前时间
     * @return 有效订阅或 null
     */
    Subscription findActiveValidByAccountId(Long accountId, LocalDateTime now);
}
