package cn.cyc.ai.cog.platform.billing.dto;

import lombok.Data;

@Data
public class AppPayOrderRequest {

    private Long orderId;

    /** MOCK / WECHAT / ALIPAY */
    private String payChannel;
    private String remark;
}
