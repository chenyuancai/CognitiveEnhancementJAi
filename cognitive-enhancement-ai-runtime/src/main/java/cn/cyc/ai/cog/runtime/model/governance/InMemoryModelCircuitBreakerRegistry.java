package cn.cyc.ai.cog.runtime.model.governance;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 租户内 JVM 内存模型熔断状态注册表。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class InMemoryModelCircuitBreakerRegistry {

    /** clock。 */
    private final Clock clock;
    private final Map<String, CircuitBreakerEntry> entries = new ConcurrentHashMap<>();

    /**
     * 创建InMemoryModelCircuitBreakerRegistry。
     */
    public InMemoryModelCircuitBreakerRegistry() {
        this(Clock.systemUTC());
    }

    /**
     * 创建InMemoryModelCircuitBreakerRegistry。
     *
     * @param clock clock
     */
    InMemoryModelCircuitBreakerRegistry(Clock clock) {
        this.clock = clock;
    }

    /**
     * 读取当前有效熔断状态。
     *
     * @param tenantCode 租户编码
     * @param modelCode  模型编码
     * @return 熔断状态快照
     */
    public CircuitBreakerSnapshot snapshot(String tenantCode, String modelCode) {
        CircuitBreakerEntry entry = entry(tenantCode, modelCode);
        refreshHalfOpen(entry, entry.openDurationMs);
        return new CircuitBreakerSnapshot(
                entry.state,
                entry.consecutiveFailureCount,
                entry.openedAt
        );
    }

    /**
     * 记录一次成功调用，重置熔断计数。
     *
     * @param tenantCode 租户编码
     * @param modelCode  模型编码
     */
    public void recordSuccess(String tenantCode, String modelCode) {
        CircuitBreakerEntry entry = entry(tenantCode, modelCode);
        entry.state = ModelCircuitBreakerState.CLOSED;
        entry.consecutiveFailureCount = 0;
        entry.openedAt = null;
        entry.openDurationMs = 0L;
    }

    /**
     * 记录一次失败调用，必要时打开熔断。
     *
     * @param tenantCode       租户编码
     * @param modelCode        模型编码
     * @param failureThreshold 失败阈值
     * @param openDurationMs   打开时长
     */
    public void recordFailure(String tenantCode,
                              String modelCode,
                              int failureThreshold,
                              long openDurationMs) {
        CircuitBreakerEntry entry = entry(tenantCode, modelCode);
        refreshHalfOpen(entry, openDurationMs);
        if (entry.state == ModelCircuitBreakerState.OPEN) {
            return;
        }
        if (entry.state == ModelCircuitBreakerState.HALF_OPEN) {
            openCircuit(entry, openDurationMs);
            return;
        }
        entry.consecutiveFailureCount++;
        if (entry.consecutiveFailureCount >= failureThreshold) {
            openCircuit(entry, openDurationMs);
        }
    }

    /**
     * 执行openCircuit。
     *
     * @param entry entry
     * @param openDurationMs openDurationMs
     */
    private void openCircuit(CircuitBreakerEntry entry, long openDurationMs) {
        entry.state = ModelCircuitBreakerState.OPEN;
        entry.openedAt = Instant.now(clock);
        entry.openDurationMs = openDurationMs;
        entry.consecutiveFailureCount = Math.max(entry.consecutiveFailureCount, 1);
    }

    /**
     * 执行refreshHalfOpen。
     *
     * @param entry entry
     * @param openDurationMs openDurationMs
     */
    private void refreshHalfOpen(CircuitBreakerEntry entry, long openDurationMs) {
        if (entry.state != ModelCircuitBreakerState.OPEN || entry.openedAt == null) {
            return;
        }
        long durationMs = entry.openDurationMs > 0 ? entry.openDurationMs : openDurationMs;
        Instant reopenAt = entry.openedAt.plusMillis(Math.max(durationMs, 1L));
        if (!Instant.now(clock).isBefore(reopenAt)) {
            entry.state = ModelCircuitBreakerState.HALF_OPEN;
        }
    }

    /**
     * 执行entry。
     *
     * @param tenantCode 租户编码
     * @param modelCode 模型编码
     * @return 执行结果
     */
    private CircuitBreakerEntry entry(String tenantCode, String modelCode) {
        return entries.computeIfAbsent(buildKey(tenantCode, modelCode), ignored -> new CircuitBreakerEntry());
    }

    /**
     * 构建键。
     *
     * @param tenantCode 租户编码
     * @param modelCode 模型编码
     * @return 构建结果
     */
    private static String buildKey(String tenantCode, String modelCode) {
        return tenantCode + "#" + modelCode;
    }

    /**
     * 熔断状态快照。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    public record CircuitBreakerSnapshot(
            ModelCircuitBreakerState state,
            int consecutiveFailureCount,
            Instant openedAt
    ) {
    }

    /**
     * CircuitBreakerEntry
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    private static final class CircuitBreakerEntry {
        /** 状态。 */
        private ModelCircuitBreakerState state = ModelCircuitBreakerState.CLOSED;
        /** consecutive失败数量。 */
        private int consecutiveFailureCount;
        /** openedAt。 */
        private Instant openedAt;
        /** openDurationMs。 */
        private long openDurationMs;
    }
}
