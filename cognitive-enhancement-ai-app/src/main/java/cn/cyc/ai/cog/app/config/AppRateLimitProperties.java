package cn.cyc.ai.cog.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * C 端 API 限流配置（{@code cog.app.rate-limit.*}）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@ConfigurationProperties(prefix = "cog.app.rate-limit")
public class AppRateLimitProperties {

    /** 是否启用限流，默认关闭。 */
    private boolean enabled = false;

    /** 每客户端每分钟最大请求数。 */
    private int requestsPerMinute = 120;
}
