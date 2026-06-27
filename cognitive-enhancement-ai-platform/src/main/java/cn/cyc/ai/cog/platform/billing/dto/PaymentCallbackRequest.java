package cn.cyc.ai.cog.platform.billing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 支付渠道回调请求体。
 */
@Data
public class PaymentCallbackRequest {

    private String channel;

    @NotBlank
    private String orderNo;

    private Long amountFen;

    private String transactionId;

    private String signature;

    private String remark;

    /** 微信 V3 回调：Wechatpay-Timestamp */
    private String timestamp;

    /** 微信 V3 回调：Wechatpay-Nonce */
    private String nonce;
}
