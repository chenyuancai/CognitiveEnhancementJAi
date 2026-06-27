package cn.cyc.ai.cog.platform.billing.support;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 支付签名/验签工具：微信 V3 风格消息体、支付宝 RSA2 排序串。
 */
public final class PaymentSignatureSupport {

    private PaymentSignatureSupport() {
    }

    /** 回调业务 canonical 串（微信/支付宝共用业务字段）。 */
    public static String buildCallbackBusinessPayload(String orderNo, Long amountFen, String transactionId) {
        return orderNo + "|"
                + (amountFen == null ? "" : amountFen) + "|"
                + (transactionId == null ? "" : transactionId);
    }

    /** 微信 V3 通知验签消息：timestamp\\nnonce\\nbody\\n */
    public static String buildWechatV3SignMessage(String timestamp, String nonce, String body) {
        return timestamp + "\n" + nonce + "\n" + body + "\n";
    }

    /** 微信 APP 预下单 paySign 载荷。 */
    public static String buildWechatPrepaySignPayload(String orderNo,
                                                    Long amountFen,
                                                    String appId,
                                                    String timeStamp,
                                                    String nonceStr,
                                                    String prepayId) {
        return orderNo + "|"
                + (amountFen == null ? "" : amountFen) + "|"
                + appId + "|"
                + timeStamp + "|"
                + nonceStr + "|"
                + prepayId;
    }

    /** 支付宝 RSA2 排序串（key=value&...，空值跳过）。 */
    public static String buildAlipaySortedContent(Map<String, String> params) {
        return new TreeMap<>(params).entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isBlank())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }

    public static String hmacSha256Hex(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw Errors.of(PlatformErrorCode.PAYMENT_HMAC_ERROR);
        }
    }

    public static String rsaSignSha256Base64(String payload, String privateKeyPem) {
        try {
            PrivateKey privateKey = loadRsaPrivateKey(privateKeyPem);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw Errors.of(PlatformErrorCode.PAYMENT_RSA_SIGN_ERROR);
        }
    }

    public static void rsaVerifySha256(String payload, String signatureBase64, String publicKeyPem) {
        try {
            PublicKey publicKey = loadRsaPublicKey(publicKeyPem);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(payload.getBytes(StandardCharsets.UTF_8));
            byte[] signBytes = Base64.getDecoder().decode(signatureBase64);
            if (!signature.verify(signBytes)) {
                throw Errors.of(PlatformErrorCode.PAYMENT_RSA_VERIFY_FAILED);
            }
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw Errors.of(PlatformErrorCode.PAYMENT_RSA_VERIFY_ERROR);
        }
    }

    public static PublicKey loadRsaPublicKey(String pem) throws Exception {
        String normalized = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(normalized);
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
    }

    private static PrivateKey loadRsaPrivateKey(String pem) throws Exception {
        String normalized = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(normalized);
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }
}
