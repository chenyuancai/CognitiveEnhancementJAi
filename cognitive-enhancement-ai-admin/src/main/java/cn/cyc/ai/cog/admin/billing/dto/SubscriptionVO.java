package cn.cyc.ai.cog.admin.billing.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Subscription视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class SubscriptionVO {

    /** 主键 ID */
    private Long id;
    /** 租户 ID */
    private Long tenantId;
    /** 账户ID */
    private Long accountId;
    /** 订单ID */
    private Long orderId;
    /** packageID */
    private Long packageId;
    /** 等级编码。 */
    private String levelCode;
    /** 状态。 */
    private String status;
    /** phase。 */
    private String phase;
    /** startAt。 */
    private LocalDateTime startAt;
    /** endAt。 */
    private LocalDateTime endAt;
    /** autoRenew。 */
    private Boolean autoRenew;
    /** package快照JSON。 */
    private String packageSnapshotJson;
}
