package cn.cyc.ai.cog.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * C 端只读数据缓存配置（{@code cog.app.cache.*}）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@ConfigurationProperties(prefix = "cog.app.cache")
public class AppReadCacheProperties {

    /** 是否启用缓存，默认开启。 */
    private boolean enabled = true;

    /** 缓存 TTL（秒），默认 60。 */
    private int ttlSeconds = 60;
}
