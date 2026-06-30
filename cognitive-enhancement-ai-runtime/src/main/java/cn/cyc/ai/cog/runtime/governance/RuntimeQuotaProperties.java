package cn.cyc.ai.cog.runtime.governance;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Runtime 限流与配额配置。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@ConfigurationProperties(prefix = "cog.runtime.quota")
public class RuntimeQuotaProperties {

    /**
     * 是否启用运行时限流。
     */
    private boolean enabled = false;

    /**
     * 应用级每分钟调用上限。小于等于 0 表示不限制。
     */
    private int applicationLimitPerMinute = 0;

    /**
     * 能力级每分钟调用上限。小于等于 0 表示不限制。
     */
    private int capabilityLimitPerMinute = 0;
}
