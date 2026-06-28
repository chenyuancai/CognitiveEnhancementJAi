package cn.cyc.ai.cog.platform.billing.support;

import cn.cyc.ai.cog.platform.billing.config.PaymentCallbackProperties;
import cn.cyc.ai.cog.platform.billing.dto.PaymentCallbackRequest;
import cn.cyc.ai.cog.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentChannelSignatureVerifierTest {

    @Test
    void shouldVerifyMockSignature() {
        PaymentCallbackProperties properties = new PaymentCallbackProperties();
        properties.setVerifySignature(true);
        properties.setMockSecret("dev-mock-secret");
        PaymentChannelSignatureVerifier verifier = new PaymentChannelSignatureVerifier(properties);

        PaymentCallbackRequest request = new PaymentCallbackRequest();
        request.setOrderNo("ORD-1");
        request.setAmountFen(100L);
        request.setTransactionId("TX-1");
        request.setSignature("dev-mock-secret");

        assertDoesNotThrow(() -> verifier.verify("MOCK", request));
    }

    @Test
    void shouldRejectInvalidMockSignature() {
        PaymentCallbackProperties properties = new PaymentCallbackProperties();
        properties.setVerifySignature(true);
        properties.setMockSecret("dev-mock-secret");
        PaymentChannelSignatureVerifier verifier = new PaymentChannelSignatureVerifier(properties);

        PaymentCallbackRequest request = new PaymentCallbackRequest();
        request.setOrderNo("ORD-1");
        request.setSignature("bad");

        assertThrows(ServiceException.class, () -> verifier.verify("MOCK", request));
    }

    @Test
    void shouldVerifyWechatV3StyleSignature() throws Exception {
        KeyPair keyPair = generateRsaKeyPair();
        PaymentCallbackProperties properties = new PaymentCallbackProperties();
        properties.setVerifySignature(true);
        properties.setWechatPlatformPublicKeyPem(toPublicPem(keyPair));

        String timestamp = "1710000000";
        String nonce = "nonce-abc";
        String body = PaymentSignatureSupport.buildCallbackBusinessPayload("ORD-WX", 9900L, "WX-TX-1");
        String message = PaymentSignatureSupport.buildWechatV3SignMessage(timestamp, nonce, body);
        String signature = PaymentSignatureSupport.rsaSignSha256Base64(message, toPrivatePem(keyPair));

        PaymentCallbackRequest request = new PaymentCallbackRequest();
        request.setOrderNo("ORD-WX");
        request.setAmountFen(9900L);
        request.setTransactionId("WX-TX-1");
        request.setTimestamp(timestamp);
        request.setNonce(nonce);
        request.setSignature(signature);

        PaymentChannelSignatureVerifier verifier = new PaymentChannelSignatureVerifier(properties);
        assertDoesNotThrow(() -> verifier.verify("WECHAT", request));
    }

    @Test
    void shouldVerifyAlipayRsa2Signature() throws Exception {
        KeyPair keyPair = generateRsaKeyPair();
        PaymentCallbackProperties properties = new PaymentCallbackProperties();
        properties.setVerifySignature(true);
        properties.setAlipayPublicKeyPem(toPublicPem(keyPair));

        Map<String, String> params = new LinkedHashMap<>();
        params.put("amount_fen", "9900");
        params.put("order_no", "ORD-AP");
        params.put("transaction_id", "AP-TX-1");
        String content = PaymentSignatureSupport.buildAlipaySortedContent(params);
        String signature = PaymentSignatureSupport.rsaSignSha256Base64(content, toPrivatePem(keyPair));

        PaymentCallbackRequest request = new PaymentCallbackRequest();
        request.setOrderNo("ORD-AP");
        request.setAmountFen(9900L);
        request.setTransactionId("AP-TX-1");
        request.setSignature(signature);

        PaymentChannelSignatureVerifier verifier = new PaymentChannelSignatureVerifier(properties);
        assertDoesNotThrow(() -> verifier.verify("ALIPAY", request));
    }

    private static KeyPair generateRsaKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private static String toPublicPem(KeyPair keyPair) {
        return "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded())
                + "\n-----END PUBLIC KEY-----";
    }

    private static String toPrivatePem(KeyPair keyPair) {
        return "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----";
    }
}
