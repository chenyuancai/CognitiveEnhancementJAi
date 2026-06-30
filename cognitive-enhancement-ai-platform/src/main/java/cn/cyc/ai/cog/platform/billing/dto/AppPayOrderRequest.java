package cn.cyc.ai.cog.platform.billing.dto;

import lombok.Data;

/**
 * C端Pay订单请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppPayOrderRequest {

    /** 订单ID */
    private Long orderId;

    /** MOCK / WECHAT / ALIPAY */
    private String payChannel;
    /** remark。 */
    private String remark;
}
