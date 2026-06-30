package cn.cyc.ai.cog.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RSA JWK 持久化路径（重启后令牌仍可验签）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@ConfigurationProperties(prefix = "cog.auth.jwk")
public class AuthJwkProperties {

    /** JWK JSON 文件路径，默认 data/cog-auth/rsa-jwk.json */
    private String keyPath = "data/cog-auth/rsa-jwk.json";
}
