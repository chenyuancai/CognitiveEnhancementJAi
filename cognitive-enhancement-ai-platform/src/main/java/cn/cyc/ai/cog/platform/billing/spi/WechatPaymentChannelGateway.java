package cn.cyc.ai.cog.platform.billing.spi;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.billing.config.PaymentCallbackProperties;
import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.dto.AppPayOrderRequest;
import cn.cyc.ai.cog.platform.billing.dto.PaymentPrepayResult;
import cn.cyc.ai.cog.api.enums.OrderStatus;
import cn.cyc.ai.cog.platform.billing.support.PaymentSignatureSupport;
import cn.cyc.ai.cog.common.exception.ServiceException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 微信支付通道：生成 APP 调起参数（canonical HMAC paySign，生产可换官方 SDK 预下单）。
 */
@Component
@org.springframework.core.annotation.Order(10)
public class WechatPaymentChannelGateway implements PaymentChannelGateway {

    private final PaymentCallbackProperties properties;

    public WechatPaymentChannelGateway(PaymentCallbackProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean supports(String channel) {
        return "WECHAT".equalsIgnoreCase(channel);
    }

    @Override
    public PaymentPrepayResult createPrepay(Order order, AppPayOrderRequest request) {
        requireWechatConfig();
        String appId = properties.getWechatAppId();
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        String prepayId = "wx" + order.orderNo().replace("-", "");
        String packageValue = "prepay_id=" + prepayId;
        String paySign = PaymentSignatureSupport.hmacSha256Hex(
                PaymentSignatureSupport.buildWechatPrepaySignPayload(
                        order.orderNo(), order.amountFen(), appId, timeStamp, nonceStr, prepayId),
                properties.getWechatApiV3Key());

        Map<String, String> clientParams = new LinkedHashMap<>();
        clientParams.put("appId", appId);
        clientParams.put("partnerId", properties.getWechatMchId());
        clientParams.put("prepayId", prepayId);
        clientParams.put("package", packageValue);
        clientParams.put("nonceStr", nonceStr);
        clientParams.put("timeStamp", timeStamp);
        clientParams.put("signType", "HMAC-SHA256");
        clientParams.put("paySign", paySign);

        return new PaymentPrepayResult(
                order.id(),
                order.orderNo(),
                "WECHAT",
                OrderStatus.PENDING.code(),
                order.amountFen(),
                order.currency(),
                prepayId,
                clientParams);
    }

    private void requireWechatConfig() {
        if (!StringUtils.hasText(properties.getWechatAppId())
                || !StringUtils.hasText(properties.getWechatMchId())
                || !StringUtils.hasText(properties.getWechatApiV3Key())) {
            throw Errors.of(PlatformErrorCode.PAYMENT_WECHAT_NOT_CONFIGURED);
        }
    }
}
