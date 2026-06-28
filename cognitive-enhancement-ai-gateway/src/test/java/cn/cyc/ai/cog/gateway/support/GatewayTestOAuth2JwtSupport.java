package cn.cyc.ai.cog.gateway.support;

import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * 网关集成测试用 OAuth2 RS256 JWT 与 JWK Set。
 */
public final class GatewayTestOAuth2JwtSupport {

    private static final RSAKey RSA_KEY;

    static {
        try {
            RSA_KEY = new RSAKeyGenerator(2048).keyID("gateway-test-key").generate();
        } catch (Exception ex) {
            throw new IllegalStateException("初始化测试 RSA 密钥失败", ex);
        }
    }

    private GatewayTestOAuth2JwtSupport() {
    }

    public static RSAPublicKey rsaPublicKey() {
        try {
            return RSA_KEY.toRSAPublicKey();
        } catch (Exception ex) {
            throw new IllegalStateException("读取测试 RSA 公钥失败", ex);
        }
    }

    public static String jwkSetJson() {
        try {
            return new JWKSet(RSA_KEY.toPublicJWK()).toString();
        } catch (Exception ex) {
            throw new IllegalStateException("导出 JWK Set 失败", ex);
        }
    }

    public static String oauth2BearerToken() {
        return oauth2BearerToken(1L, "admin", "default", List.of("ADMIN"));
    }

    public static String oauth2BearerToken(Long userId, String username, String tenantCode, List<String> roles) {
        try {
            Instant now = Instant.now();
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusSeconds(3600)))
                    .claim(SecurityConstants.CLAIM_USER_ID, userId)
                    .claim(SecurityConstants.CLAIM_USERNAME, username)
                    .claim(SecurityConstants.CLAIM_TENANT, tenantCode)
                    .claim(SecurityConstants.CLAIM_ROLES, roles)
                    .build();
            SignedJWT signedJwt = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(RSA_KEY.getKeyID()).build(),
                    claims);
            signedJwt.sign(new RSASSASigner(RSA_KEY.toRSAPrivateKey()));
            return SecurityConstants.BEARER_PREFIX + signedJwt.serialize();
        } catch (Exception ex) {
            throw new IllegalStateException("签发 OAuth2 测试 JWT 失败", ex);
        }
    }
}
