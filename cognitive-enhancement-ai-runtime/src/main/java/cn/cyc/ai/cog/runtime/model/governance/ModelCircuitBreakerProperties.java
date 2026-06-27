package cn.cyc.ai.cog.runtime.model.governance;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 模型熔断配置。
 *
 * @author cyc
 */
@Data
@ConfigurationProperties(prefix = "cog.runtime.model.circuit-breaker")
public class ModelCircuitBreakerProperties {

    /**
     * 是否启用运行时模型熔断与降级。
     */
    private boolean enabled = true;

    /**
     * 连续失败次数阈值，达到后熔断打开。
     */
    private int failureThreshold = 3;

    /**
     * 熔断打开持续时间（毫秒），到期后进入半开。
     */
    private long openDurationMs = 60_000L;
}
