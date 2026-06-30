package cn.cyc.ai.cog.sse.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SSE 服务配置（{@code cog.sse.*}）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@ConfigurationProperties(prefix = "cog.sse")
public class SseProperties {

    /** 连接超时（毫秒），默认 10 分钟。 */
    private long connectionTimeoutMs = 10 * 60 * 1000L;

    /** 心跳间隔（毫秒）。 */
    private long heartbeatIntervalMs = 30 * 1000L;

    /** 无 Token 时注入开发用户（仅本地）。 */
    private boolean devAuthBypass = true;

    /** 是否信任网关透传的 X-User-* 头。 */
    private boolean trustGatewayHeaders = true;

    /** OAuth2 JWK 端点（配置后可本地解码 RSA JWT）。 */
    private String oauth2JwkSetUri;
}
