package cn.cyc.ai.cog.platform.billing.spi;

import cn.cyc.ai.cog.api.enums.OrderStatus;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.billing.config.PaymentCallbackProperties;
import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.dto.AppPayOrderRequest;
import cn.cyc.ai.cog.platform.billing.dto.PaymentPrepayResult;
import cn.cyc.ai.cog.platform.billing.support.PaymentSignatureSupport;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 支付宝通道：生成 APP orderStr（RSA2 排序串签名，生产可换官方 SDK）。
 */
@Component
@org.springframework.core.annotation.Order(10)
public class AlipayPaymentChannelGateway implements PaymentChannelGateway {

    private final PaymentCallbackProperties properties;

    public AlipayPaymentChannelGateway(PaymentCallbackProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean supports(String channel) {
        return "ALIPAY".equalsIgnoreCase(channel);
    }

    @Override
    public PaymentPrepayResult createPrepay(Order order, AppPayOrderRequest request) {
        requireAlipayConfig();
        Map<String, String> biz = new TreeMap<>();
        biz.put("app_id", properties.getAlipayAppId());
        biz.put("method", "alipay.trade.app.pay");
        biz.put("format", "JSON");
        biz.put("charset", "utf-8");
        biz.put("sign_type", "RSA2");
        biz.put("timestamp", java.time.LocalDateTime.now().toString());
        biz.put("version", "1.0");
        biz.put("out_trade_no", order.orderNo());
        biz.put("total_amount", formatAmountYuan(order.amountFen()));
        String signContent = PaymentSignatureSupport.buildAlipaySortedContent(biz);
        String sign = PaymentSignatureSupport.rsaSignSha256Base64(signContent, properties.getAlipayMerchantPrivateKeyPem());
        biz.put("sign", sign);

        String orderStr = PaymentSignatureSupport.buildAlipaySortedContent(biz);
        Map<String, String> clientParams = new LinkedHashMap<>();
        clientParams.put("orderStr", orderStr);
        clientParams.put("appId", properties.getAlipayAppId());

        return new PaymentPrepayResult(
                order.id(),
                order.orderNo(),
                "ALIPAY",
                OrderStatus.PENDING.code(),
                order.amountFen(),
                order.currency(),
                order.orderNo(),
                clientParams);
    }

    private void requireAlipayConfig() {
        if (!StringUtils.hasText(properties.getAlipayAppId())
                || !StringUtils.hasText(properties.getAlipayMerchantPrivateKeyPem())) {
            throw Errors.of(PlatformErrorCode.PAYMENT_ALIPAY_NOT_CONFIGURED);
        }
    }

    private static String formatAmountYuan(Long amountFen) {
        if (amountFen == null) {
            return "0.00";
        }
        return String.format("%.2f", amountFen / 100.0);
    }
}
