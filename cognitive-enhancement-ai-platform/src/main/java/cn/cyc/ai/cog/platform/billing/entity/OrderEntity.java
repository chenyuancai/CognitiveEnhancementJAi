package cn.cyc.ai.cog.platform.billing.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 订单实体（映射 qz_bill_order）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_bill_order")
public class OrderEntity extends BaseEntity {

    private String orderNo;
    private Long accountId;
    private Long buyerUserId;
    private String orderType;
    private Long packageId;
    private String packageSnapshotJson;
    private Long amountFen;
    private String currency;
    private String status;
    private String payChannel;
    private LocalDateTime payTime;
    private LocalDateTime fulfillTime;
    private String idempotencyKey;
    private Long refundAmountFen;
    private LocalDateTime refundTime;
    private String remark;
}
