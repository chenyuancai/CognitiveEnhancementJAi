package cn.cyc.ai.cog.platform.billing.dto;

import lombok.Data;

/**
 * 手动标记订单已支付请求。
 *
 * @author cyc
 */
@Data
public class MarkPaidRequest {

    private Long orderId;

    /** 支付渠道，默认 MANUAL。 */
    private String payChannel;

    /** 操作备注。 */
    private String remark;
}
