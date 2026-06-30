package cn.cyc.ai.cog.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 管理后台认证配置属性
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@ConfigurationProperties(prefix = "cog.admin")
public class AdminAuthProperties {

    /** 无 Token 时注入开发管理员（仅本地/Swagger），生产务必 false。 */
    private boolean devAuthBypass = true;

    /** 是否信任网关透传的 X-User-* 头（直连单体时应 false 或必须携带 Bearer）。 */
    private boolean trustGatewayHeaders = true;

    /** OAuth2 JWK 端点（配置后 Admin 可本地解码 RSA JWT）。 */
    private String oauth2JwkSetUri;
}
