package cn.cyc.ai.cog.runtime.api;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.runtime.model.governance.ModelCircuitBreakerState;

/**
 * 模型治理解析结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ModelGovernanceResolution(
        ModelDefinition resolvedModel,
        String primaryModelCode,
        String resolvedModelCode,
        boolean fallbackApplied,
        ModelCircuitBreakerState circuitState
) {
}
