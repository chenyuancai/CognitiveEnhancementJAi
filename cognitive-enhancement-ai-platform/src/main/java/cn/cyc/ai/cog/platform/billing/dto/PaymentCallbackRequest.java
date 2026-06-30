package cn.cyc.ai.cog.platform.billing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 支付渠道回调请求体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class PaymentCallbackRequest {

    /** channel。 */
    private String channel;

    /** 订单No。 */
    @NotBlank
    private String orderNo;

    /** amountFen。 */
    private Long amountFen;

    /** transactionID */
    private String transactionId;

    /** signature。 */
    private String signature;

    /** remark。 */
    private String remark;

    /** 微信 V3 回调：Wechatpay-Timestamp */
    private String timestamp;

    /** 微信 V3 回调：Wechatpay-Nonce */
    private String nonce;
}
