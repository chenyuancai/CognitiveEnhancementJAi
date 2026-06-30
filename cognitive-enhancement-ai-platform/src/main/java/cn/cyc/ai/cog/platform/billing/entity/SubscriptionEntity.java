package cn.cyc.ai.cog.platform.billing.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Subscription实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_bill_subscription")
public class SubscriptionEntity extends BaseEntity {

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
