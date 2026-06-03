package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.harness.OutputGovernance;
import cn.cyc.ai.cog.core.harness.PolicyHarness;
import cn.cyc.ai.cog.core.harness.RuntimeHarness;
import cn.cyc.ai.cog.core.harness.TraceHarness;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.core.trace.TraceContext;
import cn.cyc.ai.cog.runtime.spi.CapabilityRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * RuntimeHarness 默认实现，协调各治理组件完成带治理的执行。
 *
 * @author cyc
 */
@Component
public class DefaultRuntimeHarness implements RuntimeHarness {

    private static final Logger log = LoggerFactory.getLogger(DefaultRuntimeHarness.class);

    private final TraceHarness traceHarness;
    private final PolicyHarness policyHarness;
    private final CapabilityDefinitionRepository capabilityDefinitionRepository;
    private final CapabilityRuntime capabilityRuntime;
    private final OutputGovernance outputGovernance;

    public DefaultRuntimeHarness(TraceHarness traceHarness,
                                 PolicyHarness policyHarness,
                                 CapabilityDefinitionRepository capabilityDefinitionRepository,
                                 CapabilityRuntime capabilityRuntime,
                                 OutputGovernance outputGovernance) {
        this.traceHarness = traceHarness;
        this.policyHarness = policyHarness;
        this.capabilityDefinitionRepository = capabilityDefinitionRepository;
        this.capabilityRuntime = capabilityRuntime;
        this.outputGovernance = outputGovernance;
    }

    @Override
    public CapabilityExecuteResponse execute(CapabilityExecuteRequest request) {
        String traceId = TraceContext.getTraceId();
        TraceHarness.TraceHarnessContext traceContext = traceHarness.start(traceId,
                Map.of("capabilityCode", request.capabilityCode()));

        try {
            CapabilityDefinition capability = capabilityDefinitionRepository.findByCode(request.capabilityCode())
                    .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到能力: " + request.capabilityCode()));
            if (capability.status() != CommonStatus.ENABLED) {
                throw new BusinessException("CONFLICT", "能力未启用: " + request.capabilityCode());
            }

            ExecutionContext execContext = new ExecutionContext(
                    traceContext.traceId(), request, capability, null, null, null, Map.of()
            );
            PolicyHarness.PolicyDecision decision = policyHarness.evaluate(capability, execContext);
            if (!decision.allowed()) {
                throw new BusinessException("FORBIDDEN", "策略检查未通过: " + decision.reason());
            }

            CapabilityExecuteResponse response = capabilityRuntime.execute(request);

            ExecutionResult governedResult = outputGovernance.govern(response.result(), null);

            return new CapabilityExecuteResponse(
                    response.traceId(),
                    response.capability(),
                    response.agent(),
                    governedResult
            );
        } finally {
            traceHarness.finish(traceContext);
        }
    }
}
