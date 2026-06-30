package cn.cyc.ai.cog.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * C 端鉴权配置（{@code cog.app.*}）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@ConfigurationProperties(prefix = "cog.app")
public class AppAuthProperties {

    /** 无 Token 时注入开发用户（仅本地/Swagger），生产务必 false */
    private boolean devAuthBypass = true;

    /** 是否信任网关透传的 X-User-* 头 */
    private boolean trustGatewayHeaders = true;

    /** OAuth2 JWK 端点（配置后 App 可本地解码 RSA JWT） */
    private String oauth2JwkSetUri;
}
