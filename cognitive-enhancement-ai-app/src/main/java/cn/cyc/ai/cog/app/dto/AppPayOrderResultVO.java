package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.util.Map;

/**
 * C 端预支付结果 VO。
 */
@Data
public class AppPayOrderResultVO {

    private Long orderId;
    private String orderNo;
    private String payChannel;
    private String status;
    private Long amountFen;
    private String currency;
    private String prepayId;
    private Map<String, String> clientParams;
}
