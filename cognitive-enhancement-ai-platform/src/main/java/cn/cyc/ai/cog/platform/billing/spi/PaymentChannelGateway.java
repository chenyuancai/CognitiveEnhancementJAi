package cn.cyc.ai.cog.platform.billing.spi;

import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.dto.AppPayOrderRequest;
import cn.cyc.ai.cog.platform.billing.dto.PaymentPrepayResult;

/**
 * 支付通道 SPI：各渠道负责预下单与客户端调起参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface PaymentChannelGateway {

    /** 是否支持该通道（MOCK / WECHAT / ALIPAY）。 */
    boolean supports(String channel);

    /** 创建预支付单，订单保持 PENDING。 */
    PaymentPrepayResult createPrepay(Order order, AppPayOrderRequest request);
}
