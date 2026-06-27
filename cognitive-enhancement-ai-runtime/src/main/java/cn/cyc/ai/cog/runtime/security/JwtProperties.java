package cn.cyc.ai.cog.runtime.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * JWT 配置属性。
 *
 * @author cyc
 */
@Data
@ConfigurationProperties(prefix = "cog.jwt")
public class JwtProperties {

    /**
     * 是否启用 API JWT 鉴权（测试环境可关闭）。
     */
    private boolean authEnabled = true;

    /**
     * 免鉴权路径前缀（Ant 风格，如 /api/auth/login）。
     */
    private List<String> permitAll = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/users/login",
            "/api/users/register",
            "/doc.html",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/api/app/billing/pay-callback/**"
    );

    /**
     * 是否信任网关透传的身份头（X-User-Id 等）；为 true 时无 Bearer 也可放行已由网关验签的请求。
     */
    private boolean trustGatewayHeaders = false;

    /**
     * JWT 签名密钥（至少 32 字符）。
     */
    private String secret = "cognitive-enhancement-ai-default-secret-key";

    /**
     * Token 有效期（毫秒），默认 24 小时。
     */
    private long expirationMs = 86400000;
}
