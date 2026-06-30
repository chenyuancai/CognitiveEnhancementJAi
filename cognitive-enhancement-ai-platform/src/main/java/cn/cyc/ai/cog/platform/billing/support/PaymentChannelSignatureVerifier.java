package cn.cyc.ai.cog.platform.billing.support;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.billing.config.PaymentCallbackProperties;
import cn.cyc.ai.cog.platform.billing.dto.PaymentCallbackRequest;
import cn.cyc.ai.cog.common.exception.ServiceException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 支付回调验签：MOCK 明文、微信 V3 风格 RSA、支付宝 RSA2 排序串。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class PaymentChannelSignatureVerifier {

    /** properties。 */
    private final PaymentCallbackProperties properties;

    /**
     * 创建PaymentChannelSignatureVerifier。
     *
     * @param properties properties
     */
    public PaymentChannelSignatureVerifier(PaymentCallbackProperties properties) {
        this.properties = properties;
    }

    /**
     * 执行verify。
     *
     * @param channel channel
     * @param request 请求
     */
    public void verify(String channel, PaymentCallbackRequest request) {
        if (!properties.isVerifySignature()) {
            return;
        }
        if ("MOCK".equals(channel)) {
            verifyMock(request);
            return;
        }
        if ("WECHAT".equals(channel)) {
            verifyWechat(request);
            return;
        }
        if ("ALIPAY".equals(channel)) {
            verifyAlipay(request);
            return;
        }
        throw Errors.of(PlatformErrorCode.PAYMENT_CHANNEL_UNSUPPORTED, "不支持的支付通道：" + channel);
    }

    /**
     * 执行verifyMock。
     *
     * @param request 请求
     */
    private void verifyMock(PaymentCallbackRequest request) {
        String secret = properties.getMockSecret();
        if (!StringUtils.hasText(secret)) {
            return;
        }
        if (!secret.equals(request.getSignature())) {
            throw Errors.of(PlatformErrorCode.PAYMENT_MOCK_VERIFY_FAILED);
        }
    }

    /**
     * 执行verifyWechat。
     *
     * @param request 请求
     */
    private void verifyWechat(PaymentCallbackRequest request) {
        String publicKeyPem = properties.getWechatPlatformPublicKeyPem();
        if (!StringUtils.hasText(publicKeyPem)) {
            throw Errors.of(PlatformErrorCode.PAYMENT_WECHAT_PUBKEY_MISSING);
        }
        if (!StringUtils.hasText(request.getSignature())) {
            throw Errors.of(PlatformErrorCode.PAYMENT_WECHAT_SIGNATURE_MISSING);
        }
        if (!StringUtils.hasText(request.getTimestamp()) || !StringUtils.hasText(request.getNonce())) {
            throw Errors.of(PlatformErrorCode.PAYMENT_WECHAT_TIMESTAMP_MISSING);
        }
        String body = PaymentSignatureSupport.buildCallbackBusinessPayload(
                request.getOrderNo(), request.getAmountFen(), request.getTransactionId());
        String message = PaymentSignatureSupport.buildWechatV3SignMessage(
                request.getTimestamp(), request.getNonce(), body);
        PaymentSignatureSupport.rsaVerifySha256(message, request.getSignature(), publicKeyPem);
    }

    /**
     * 执行verifyAlipay。
     *
     * @param request 请求
     */
    private void verifyAlipay(PaymentCallbackRequest request) {
        String publicKeyPem = properties.getAlipayPublicKeyPem();
        if (!StringUtils.hasText(publicKeyPem)) {
            throw Errors.of(PlatformErrorCode.PAYMENT_ALIPAY_PUBKEY_MISSING);
        }
        if (!StringUtils.hasText(request.getSignature())) {
            throw Errors.of(PlatformErrorCode.PAYMENT_ALIPAY_SIGNATURE_MISSING);
        }
        Map<String, String> params = new LinkedHashMap<>();
        params.put("amount_fen", request.getAmountFen() == null ? null : String.valueOf(request.getAmountFen()));
        params.put("order_no", request.getOrderNo());
        params.put("transaction_id", request.getTransactionId());
        String content = PaymentSignatureSupport.buildAlipaySortedContent(params);
        PaymentSignatureSupport.rsaVerifySha256(content, request.getSignature(), publicKeyPem);
    }
}
