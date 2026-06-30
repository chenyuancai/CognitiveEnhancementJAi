package cn.cyc.ai.cog.platform.billing.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 支付回调验签配置（占位，生产需对接真实渠道密钥）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@ConfigurationProperties(prefix = "cog.billing.pay-callback")
public class PaymentCallbackProperties {

    /** MOCK 通道共享密钥，空则跳过验签（仅开发）。 */
    private String mockSecret = "dev-mock-secret";

    /** 是否启用验签。 */
    private boolean verifySignature = true;

    /** 微信 AppId。 */
    private String wechatAppId;

    /** 微信商户号。 */
    private String wechatMchId;

    /** 微信 APIv3 Key（预下单 paySign HMAC）。 */
    private String wechatApiV3Key;

    /** 微信平台公钥 PEM（回调 RSA 验签，V3 通知风格）。 */
    private String wechatPlatformPublicKeyPem;

    /** 支付宝 AppId。 */
    private String alipayAppId;

    /** 支付宝商户私钥 PEM（预下单 RSA2 签名）。 */
    private String alipayMerchantPrivateKeyPem;

    /** 支付宝 RSA 公钥 PEM（回调 RSA2 验签）。 */
    private String alipayPublicKeyPem;
}
