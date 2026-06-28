package cn.cyc.ai.cog.platform.billing.spi;

import cn.cyc.ai.cog.platform.billing.config.PaymentCallbackProperties;
import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.dto.AppPayOrderRequest;
import cn.cyc.ai.cog.platform.billing.dto.PaymentPrepayResult;
import cn.cyc.ai.cog.api.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentChannelGatewayTest {

    @Test
    void shouldCreateMockPrepayWithoutMarkingPaid() {
        PaymentCallbackProperties properties = new PaymentCallbackProperties();
        MockPaymentChannelGateway gateway = new MockPaymentChannelGateway(properties);
        Order order = sampleOrder();

        PaymentPrepayResult result = gateway.createPrepay(order, new AppPayOrderRequest());

        assertEquals(OrderStatus.PENDING.code(), result.status());
        assertEquals("MOCK", result.payChannel());
        assertNotNull(result.clientParams().get("mockPayToken"));
    }

    @Test
    void shouldCreateWechatPrepayParams() {
        PaymentCallbackProperties properties = new PaymentCallbackProperties();
        properties.setWechatAppId("wx-test-app");
        properties.setWechatMchId("1900000109");
        properties.setWechatApiV3Key("test-api-v3-key-32bytes-long!!");
        WechatPaymentChannelGateway gateway = new WechatPaymentChannelGateway(properties);

        PaymentPrepayResult result = gateway.createPrepay(sampleOrder(), payRequest("WECHAT"));

        assertEquals("WECHAT", result.payChannel());
        assertNotNull(result.clientParams().get("paySign"));
        assertTrue(result.clientParams().get("package").startsWith("prepay_id="));
    }

    @Test
    void shouldCreateAlipayOrderStr() throws Exception {
        var keyPair = java.security.KeyPairGenerator.getInstance("RSA");
        keyPair.initialize(2048);
        var pair = keyPair.generateKeyPair();
        String privatePem = "-----BEGIN PRIVATE KEY-----\n"
                + java.util.Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----";

        PaymentCallbackProperties properties = new PaymentCallbackProperties();
        properties.setAlipayAppId("2021000000000000");
        properties.setAlipayMerchantPrivateKeyPem(privatePem);
        AlipayPaymentChannelGateway gateway = new AlipayPaymentChannelGateway(properties);

        PaymentPrepayResult result = gateway.createPrepay(sampleOrder(), payRequest("ALIPAY"));

        assertEquals("ALIPAY", result.payChannel());
        assertNotNull(result.clientParams().get("orderStr"));
        assertTrue(result.clientParams().get("orderStr").contains("sign="));
    }

    private static AppPayOrderRequest payRequest(String channel) {
        AppPayOrderRequest request = new AppPayOrderRequest();
        request.setPayChannel(channel);
        return request;
    }

    private static Order sampleOrder() {
        return new Order(
                1L, 1L, "ORD-TEST-001", 1L, 1L,
                "QUOTA", 1L, "{}", 9900L, "CNY",
                OrderStatus.PENDING.code(), null, null, null,
                null, null, null, null);
    }
}
