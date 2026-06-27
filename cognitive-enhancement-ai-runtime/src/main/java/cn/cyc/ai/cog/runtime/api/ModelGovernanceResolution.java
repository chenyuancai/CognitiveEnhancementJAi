package cn.cyc.ai.cog.runtime.api;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.runtime.model.governance.ModelCircuitBreakerState;

/**
 * 模型治理解析结果。
 *
 * @param resolvedModel      本次实际调用的模型定义（含 timeoutMs）
 * @param primaryModelCode   主模型编码
 * @param resolvedModelCode  解析后的模型编码
 * @param fallbackApplied    是否已降级到 fallback
 * @param circuitState       主模型熔断状态
 * @author cyc
 */
public record ModelGovernanceResolution(
        ModelDefinition resolvedModel,
        String primaryModelCode,
        String resolvedModelCode,
        boolean fallbackApplied,
        ModelCircuitBreakerState circuitState
) {
}
