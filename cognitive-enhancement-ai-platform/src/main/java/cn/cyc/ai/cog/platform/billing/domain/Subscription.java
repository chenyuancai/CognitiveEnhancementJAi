package cn.cyc.ai.cog.platform.billing.domain;

import java.time.LocalDateTime;

/**
 * 订阅记录领域对象。
 *
 * @param id                  订阅 ID
 * @param tenantId            租户 ID
 * @param accountId           商业账户 ID
 * @param orderId             关联订单 ID
 * @param packageId           套餐 ID
 * @param levelCode           会员等级编码
 * @param status              订阅状态
 * @param phase               周期阶段（TRIAL/FORMAL）
 * @param startAt             生效时间
 * @param endAt               到期时间
 * @param autoRenew           是否自动续费
 * @param packageSnapshotJson 套餐快照 JSON
 */
public record Subscription(
        Long id,
        Long tenantId,
        Long accountId,
        Long orderId,
        Long packageId,
        String levelCode,
        String status,
        String phase,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Boolean autoRenew,
        String packageSnapshotJson
) {
}
