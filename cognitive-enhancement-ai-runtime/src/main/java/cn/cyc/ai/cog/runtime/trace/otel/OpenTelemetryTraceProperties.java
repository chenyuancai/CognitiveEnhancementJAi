package cn.cyc.ai.cog.runtime.trace.otel;

import cn.cyc.ai.cog.runtime.trace.domain.TraceSpan;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OpenTelemetry TraceSpan 导出配置。
 *
 * @author cyc
 */
@Data
@ConfigurationProperties(prefix = "cog.runtime.trace.otel")
public class OpenTelemetryTraceProperties {

    /**
     * 是否启用 TraceSpan OTLP HTTP 导出。
     */
    private boolean enabled = false;

    /**
     * OTLP HTTP 接收端点，例如 http://127.0.0.1:4318/v1/traces。
     */
    private String endpoint = "http://127.0.0.1:4318/v1/traces";

    /**
     * HTTP 请求超时（毫秒）。
     */
    private long timeoutMs = 3000L;
}
