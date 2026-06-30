package cn.cyc.ai.cog.runtime.api;

import cn.cyc.ai.cog.runtime.model.governance.ModelCircuitBreakerState;

import java.time.Instant;

/**
 * 模型治理状态查询结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ModelGovernanceStateResult(
        String tenantCode,
        String modelCode,
        String fallbackModelCode,
        ModelCircuitBreakerState circuitState,
        int consecutiveFailureCount,
        Instant openedAt
) {
}
