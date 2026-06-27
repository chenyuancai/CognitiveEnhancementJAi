package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * C 端订单展示 VO。
 */
@Data
public class OrderVO {

    /** 订单 ID */
    private Long id;

    /** 租户 ID */
    private Long tenantId;

    /** 订单号 */
    private String orderNo;

    /** 商业账户 ID */
    private Long accountId;

    /** 下单用户 ID */
    private Long buyerUserId;

    /** 订单类型（订阅/额度包等） */
    private String orderType;

    /** 套餐 ID */
    private Long packageId;

    /** 下单时套餐快照 JSON */
    private String packageSnapshotJson;

    /** 应付金额（分） */
    private Long amountFen;

    /** 币种 */
    private String currency;

    /** 订单状态 */
    private String status;

    /** 支付渠道 */
    private String payChannel;

    /** 支付时间 */
    private LocalDateTime payTime;

    /** 履约完成时间 */
    private LocalDateTime fulfillTime;

    /** 幂等键 */
    private String idempotencyKey;

    /** 退款金额（分） */
    private Long refundAmountFen;

    /** 退款时间 */
    private LocalDateTime refundTime;

    /** 备注 */
    private String remark;
}
