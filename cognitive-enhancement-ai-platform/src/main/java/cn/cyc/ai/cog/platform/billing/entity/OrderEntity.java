package cn.cyc.ai.cog.platform.billing.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 订单实体（映射 qz_bill_order）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_bill_order")
public class OrderEntity extends BaseEntity {

    /** 订单No。 */
    private String orderNo;
    /** 账户ID */
    private Long accountId;
    /** buyer用户ID */
    private Long buyerUserId;
    /** 订单类型。 */
    private String orderType;
    /** packageID */
    private Long packageId;
    /** package快照JSON。 */
    private String packageSnapshotJson;
    /** amountFen。 */
    private Long amountFen;
    /** currency。 */
    private String currency;
    /** 状态。 */
    private String status;
    /** payChannel。 */
    private String payChannel;
    /** pay时间。 */
    private LocalDateTime payTime;
    /** fulfill时间。 */
    private LocalDateTime fulfillTime;
    /** idempotency键。 */
    private String idempotencyKey;
    /** refundAmountFen。 */
    private Long refundAmountFen;
    /** refund时间。 */
    private LocalDateTime refundTime;
    /** remark。 */
    private String remark;
}
