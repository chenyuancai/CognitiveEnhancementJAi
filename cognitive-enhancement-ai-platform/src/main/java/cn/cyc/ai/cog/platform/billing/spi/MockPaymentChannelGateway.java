package cn.cyc.ai.cog.platform.billing.spi;

import cn.cyc.ai.cog.platform.billing.config.PaymentCallbackProperties;
import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.dto.AppPayOrderRequest;
import cn.cyc.ai.cog.platform.billing.dto.PaymentPrepayResult;
import cn.cyc.ai.cog.api.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * MOCK 通道：返回开发调试用 clientParams，支付结果由回调闭环。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
@org.springframework.core.annotation.Order(100)
public class MockPaymentChannelGateway implements PaymentChannelGateway {

    /** properties。 */
    private final PaymentCallbackProperties properties;

    /**
     * 创建MockPaymentChannelGateway。
     *
     * @param properties properties
     */
    public MockPaymentChannelGateway(PaymentCallbackProperties properties) {
        this.properties = properties;
    }

    /**
     * 执行supports。
     *
     * @param channel channel
     * @return 执行结果
     */
    @Override
    public boolean supports(String channel) {
        return "MOCK".equalsIgnoreCase(channel);
    }

    /**
     * 创建Prepay。
     *
     * @param order 订单
     * @param request 请求
     * @return 创建结果
     */
    @Override
    public PaymentPrepayResult createPrepay(Order order, AppPayOrderRequest request) {
        Map<String, String> clientParams = new LinkedHashMap<>();
        clientParams.put("channel", "MOCK");
        clientParams.put("orderNo", order.orderNo());
        clientParams.put("amountFen", String.valueOf(order.amountFen()));
        clientParams.put("mockPayToken", UUID.randomUUID().toString());
        clientParams.put("callbackPath", "/api/app/billing/pay-callback/MOCK");
        clientParams.put("signatureHint", properties.getMockSecret());
        return new PaymentPrepayResult(
                order.id(),
                order.orderNo(),
                "MOCK",
                OrderStatus.PENDING.code(),
                order.amountFen(),
                order.currency(),
                "mock-" + order.orderNo(),
                clientParams);
    }
}
