package cn.cyc.ai.cog.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 网关侧 legacy JWT 配置，需与单体 {@code cog.jwt.secret} 保持一致。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@ConfigurationProperties(prefix = "cog.jwt")
public class GatewayJwtProperties {

    /** HS256 签名密钥（与 runtime JwtUtil 一致）。 */
    private String secret = "cognitive-enhancement-ai-default-secret-key";
}
