package cn.cyc.ai.cog.platform.billing.dto;

import java.util.Map;

/**
 * 预支付结果：供客户端调起微信/支付宝/MOCK 支付。
 */
public record PaymentPrepayResult(
        Long orderId,
        String orderNo,
        String payChannel,
        String status,
        Long amountFen,
        String currency,
        String prepayId,
        Map<String, String> clientParams
) {
}
