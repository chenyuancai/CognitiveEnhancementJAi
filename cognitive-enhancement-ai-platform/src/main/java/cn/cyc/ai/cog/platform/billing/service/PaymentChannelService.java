package cn.cyc.ai.cog.platform.billing.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.dto.AppPayOrderRequest;
import cn.cyc.ai.cog.platform.billing.dto.PaymentPrepayResult;
import cn.cyc.ai.cog.platform.billing.spi.PaymentChannelGateway;
import cn.cyc.ai.cog.common.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 支付通道编排：按渠道选择 Gateway 创建预支付单。
 */
@Service
public class PaymentChannelService {

    private final List<PaymentChannelGateway> gateways;

    public PaymentChannelService(List<PaymentChannelGateway> gateways) {
        this.gateways = gateways;
    }

    public PaymentPrepayResult createPrepay(Order order, AppPayOrderRequest request) {
        String channel = normalizeChannel(request == null ? null : request.getPayChannel());
        return gateways.stream()
                .filter(gateway -> gateway.supports(channel))
                .findFirst()
                .map(gateway -> gateway.createPrepay(order, request))
                .orElseThrow(() -> Errors.of(PlatformErrorCode.PAYMENT_CHANNEL_UNSUPPORTED, "不支持的支付通道：" + channel));
    }

    private String normalizeChannel(String channel) {
        if (!StringUtils.hasText(channel)) {
            return "MOCK";
        }
        return channel.trim().toUpperCase();
    }
}
