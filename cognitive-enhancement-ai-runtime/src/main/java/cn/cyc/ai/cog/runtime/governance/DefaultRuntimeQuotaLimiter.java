package cn.cyc.ai.cog.runtime.governance;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.spi.RuntimeQuotaLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存滑动窗口的默认运行时限流器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class DefaultRuntimeQuotaLimiter implements RuntimeQuotaLimiter {

    /**
     * 窗口长度：1 分钟。
     */
    private static final Duration WINDOW = Duration.ofMinutes(1);

    /**
     * 应用级 Key 后缀。
     */
    private static final String APPLICATION_SCOPE = "__application__";

    /**
     * 限流配置。
     */
    private final RuntimeQuotaProperties properties;

    /**
     * 时钟。
     */
    private final Clock clock;

    /**
     * 各限流维度的调用时间窗口。
     */
    private final Map<String, Deque<Long>> windows = new ConcurrentHashMap<>();

    /**
     * 构造默认限流器。
     *
     * @param properties 限流配置
     */
    @Autowired
    public DefaultRuntimeQuotaLimiter(RuntimeQuotaProperties properties) {
        this(properties, Clock.systemUTC());
    }

    /**
     * 构造默认限流器。
     *
     * @param properties 限流配置
     * @param clock      时钟
     */
    DefaultRuntimeQuotaLimiter(RuntimeQuotaProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    /**
     * 检查并消费一次能力调用配额。
     *
     * @param capabilityCode 能力编码
     */
    @Override
    public void checkAndConsume(String capabilityCode) {
        if (!properties.isEnabled()) {
            return;
        }
        String tenantCode = TenantContext.currentTenantCode();
        long now = clock.millis();
        consume(applicationKey(tenantCode), properties.getApplicationLimitPerMinute(), now,
                "应用级调用频率已超过限制: " + properties.getApplicationLimitPerMinute() + " 次/分钟");
        consume(capabilityKey(tenantCode, capabilityCode), properties.getCapabilityLimitPerMinute(), now,
                "能力级调用频率已超过限制: " + capabilityCode + ", "
                        + properties.getCapabilityLimitPerMinute() + " 次/分钟");
    }

    /**
     * 消费指定维度的一次调用。
     *
     * @param key          限流 Key
     * @param limit        窗口上限
     * @param now          当前时间戳
     * @param errorMessage 超限提示
     */
    private void consume(String key, int limit, long now, String errorMessage) {
        if (limit <= 0) {
            return;
        }
        Deque<Long> window = windows.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        synchronized (window) {
            evictExpired(window, now);
            if (window.size() >= limit) {
                throw new BusinessException("TOO_MANY_REQUESTS", errorMessage);
            }
            window.addLast(now);
        }
    }

    /**
     * 清理窗口外请求。
     *
     * @param window 当前窗口
     * @param now    当前时间戳
     */
    private void evictExpired(Deque<Long> window, long now) {
        long threshold = now - WINDOW.toMillis();
        while (!window.isEmpty() && window.peekFirst() <= threshold) {
            window.removeFirst();
        }
    }

    /**
     * 构造应用级限流 Key。
     *
     * @param tenantCode 租户编码
     * @return 限流 Key
     */
    private String applicationKey(String tenantCode) {
        return tenantCode + ":" + APPLICATION_SCOPE;
    }

    /**
     * 构造能力级限流 Key。
     *
     * @param tenantCode     租户编码
     * @param capabilityCode 能力编码
     * @return 限流 Key
     */
    private String capabilityKey(String tenantCode, String capabilityCode) {
        return tenantCode + ":" + capabilityCode;
    }
}
