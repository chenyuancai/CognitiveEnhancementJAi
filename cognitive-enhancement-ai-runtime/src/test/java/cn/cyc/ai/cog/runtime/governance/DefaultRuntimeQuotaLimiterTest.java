package cn.cyc.ai.cog.runtime.governance;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 默认运行时限流与配额测试。
 *
 * @author cyc
 */
class DefaultRuntimeQuotaLimiterTest {

    /**
     * 固定测试时钟。
     */
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-10T00:00:00Z"), ZoneOffset.UTC);

    /**
     * 清理租户上下文。
     */
    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    /**
     * 验证默认关闭时不执行限流。
     */
    @Test
    void shouldAllowWhenQuotaDisabled() {
        RuntimeQuotaProperties properties = new RuntimeQuotaProperties();
        properties.setEnabled(false);
        properties.setApplicationLimitPerMinute(1);

        DefaultRuntimeQuotaLimiter limiter = new DefaultRuntimeQuotaLimiter(properties, CLOCK);

        assertDoesNotThrow(() -> {
            limiter.checkAndConsume("capability.chat.generate");
            limiter.checkAndConsume("capability.chat.generate");
        });
    }

    /**
     * 验证应用级限流按租户总调用量拒绝超限请求。
     */
    @Test
    void shouldRejectWhenApplicationLimitExceeded() {
        RuntimeQuotaProperties properties = new RuntimeQuotaProperties();
        properties.setEnabled(true);
        properties.setApplicationLimitPerMinute(1);
        DefaultRuntimeQuotaLimiter limiter = new DefaultRuntimeQuotaLimiter(properties, CLOCK);

        limiter.checkAndConsume("capability.chat.generate");
        BusinessException exception = assertThrows(BusinessException.class,
                () -> limiter.checkAndConsume("capability.qa.answer"));

        assertEquals("TOO_MANY_REQUESTS", exception.getSemanticCode());
        assertEquals("应用级调用频率已超过限制: 1 次/分钟", exception.getMessage());
    }

    /**
     * 验证能力级限流按租户和能力维度拒绝超限请求。
     */
    @Test
    void shouldRejectWhenCapabilityLimitExceeded() {
        RuntimeQuotaProperties properties = new RuntimeQuotaProperties();
        properties.setEnabled(true);
        properties.setCapabilityLimitPerMinute(1);
        DefaultRuntimeQuotaLimiter limiter = new DefaultRuntimeQuotaLimiter(properties, CLOCK);

        limiter.checkAndConsume("capability.chat.generate");
        BusinessException exception = assertThrows(BusinessException.class,
                () -> limiter.checkAndConsume("capability.chat.generate"));

        assertEquals("TOO_MANY_REQUESTS", exception.getSemanticCode());
        assertEquals("能力级调用频率已超过限制: capability.chat.generate, 1 次/分钟", exception.getMessage());
    }

    /**
     * 验证限流窗口按租户隔离。
     */
    @Test
    void shouldIsolateQuotaByTenant() {
        RuntimeQuotaProperties properties = new RuntimeQuotaProperties();
        properties.setEnabled(true);
        properties.setApplicationLimitPerMinute(1);
        DefaultRuntimeQuotaLimiter limiter = new DefaultRuntimeQuotaLimiter(properties, CLOCK);

        TenantContext.setTenantCode("tenant-a");
        limiter.checkAndConsume("capability.chat.generate");

        TenantContext.setTenantCode("tenant-b");
        assertDoesNotThrow(() -> limiter.checkAndConsume("capability.chat.generate"));
    }
}
