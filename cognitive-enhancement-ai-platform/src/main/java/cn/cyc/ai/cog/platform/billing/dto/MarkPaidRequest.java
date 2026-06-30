package cn.cyc.ai.cog.platform.billing.dto;

import lombok.Data;

/**
 * 手动标记订单已支付请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class MarkPaidRequest {

    /** 订单ID */
    private Long orderId;

    /** 支付渠道，默认 MANUAL。 */
    private String payChannel;

    /** 操作备注。 */
    private String remark;
}
