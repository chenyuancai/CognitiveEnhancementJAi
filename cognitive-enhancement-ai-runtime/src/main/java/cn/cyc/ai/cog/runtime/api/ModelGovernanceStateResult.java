package cn.cyc.ai.cog.runtime.api;

import cn.cyc.ai.cog.runtime.model.governance.ModelCircuitBreakerState;

import java.time.Instant;

/**
 * 模型治理状态查询结果。
 *
 * @param tenantCode              租户编码
 * @param modelCode               模型编码
 * @param fallbackModelCode       降级模型编码
 * @param circuitState            熔断状态
 * @param consecutiveFailureCount 连续失败次数
 * @param openedAt                熔断打开时间
 * @author cyc
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
