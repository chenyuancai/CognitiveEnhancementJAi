package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.util.Map;

/**
 * C 端预支付结果 VO。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppPayOrderResultVO {

    /** 订单ID */
    private Long orderId;
    /** 订单No。 */
    private String orderNo;
    /** payChannel。 */
    private String payChannel;
    /** 状态。 */
    private String status;
    /** amountFen。 */
    private Long amountFen;
    /** currency。 */
    private String currency;
    /** prepayID */
    private String prepayId;
    private Map<String, String> clientParams;
}
