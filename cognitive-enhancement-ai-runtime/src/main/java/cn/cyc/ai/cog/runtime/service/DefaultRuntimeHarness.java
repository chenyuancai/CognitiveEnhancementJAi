package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.harness.OutputGovernance;
import cn.cyc.ai.cog.core.harness.PolicyHarness;
import cn.cyc.ai.cog.core.harness.RuntimeHarness;
import cn.cyc.ai.cog.core.harness.TraceHarness;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.core.trace.TraceContext;
import cn.cyc.ai.cog.runtime.release.router.CapabilityVersionResolver;
import cn.cyc.ai.cog.runtime.session.service.ConversationSessionService;
import cn.cyc.ai.cog.runtime.spi.CapabilityRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * RuntimeHarness 默认实现，协调各治理组件完成带治理的执行。
 *
 * @author cyc
 */
@Component
public class DefaultRuntimeHarness implements RuntimeHarness {

    private static final Logger log = LoggerFactory.getLogger(DefaultRuntimeHarness.class);

    private static final String HUMAN_CONFIRMED_PARAMETER = "humanConfirmed";

    private final TraceHarness traceHarness;
    private final PolicyHarness policyHarness;
    private final CapabilityVersionResolver capabilityVersionResolver;
    private final CapabilityRuntime capabilityRuntime;
    private final OutputGovernance outputGovernance;
    private final ConversationSessionService conversationSessionService;

    public DefaultRuntimeHarness(TraceHarness traceHarness,
                                 PolicyHarness policyHarness,
                                 CapabilityVersionResolver capabilityVersionResolver,
                                 CapabilityRuntime capabilityRuntime,
                                 OutputGovernance outputGovernance,
                                 ConversationSessionService conversationSessionService) {
        this.traceHarness = traceHarness;
        this.policyHarness = policyHarness;
        this.capabilityVersionResolver = capabilityVersionResolver;
        this.capabilityRuntime = capabilityRuntime;
        this.outputGovernance = outputGovernance;
        this.conversationSessionService = conversationSessionService;
    }

    @Override
    public CapabilityExecuteResponse execute(CapabilityExecuteRequest request) {
        String traceId = TraceContext.getTraceId();
        TraceHarness.TraceHarnessContext traceContext = traceHarness.start(traceId,
                Map.of("capabilityCode", request.capabilityCode()));

        try {
            CapabilityDefinition capability = capabilityVersionResolver.resolve(
                    request.capabilityCode(), traceContext.traceId());
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

            CapabilityExecuteResponse response = capabilityRuntime.execute(executionRequest(request));

            ExecutionResult governedResult = outputGovernance.govern(response.result(), Map.of(
                    "riskLevel", response.capability().riskLevel().name(),
                    "needHumanConfirm", response.capability().needHumanConfirm()
            ));

            CapabilityExecuteResponse governedResponse = new CapabilityExecuteResponse(
                    response.traceId(),
                    response.capability(),
                    response.agent(),
                    governedResult
            );
            recordSessionMessages(request, governedResponse);
            return governedResponse;
        } finally {
            traceHarness.finish(traceContext);
        }
    }

    /**
     * 构造下游执行请求，剥离 Runtime 治理参数。
     *
     * @param request 原始请求
     * @return 下游执行请求
     */
    private CapabilityExecuteRequest executionRequest(CapabilityExecuteRequest request) {
        Map<String, Object> parameters = new LinkedHashMap<>(request.parameters());
        parameters.remove(HUMAN_CONFIRMED_PARAMETER);
        if (parameters.size() == request.parameters().size()) {
            return request;
        }
        return new CapabilityExecuteRequest(request.capabilityCode(), request.input(), parameters);
    }

    private void recordSessionMessages(CapabilityExecuteRequest request, CapabilityExecuteResponse response) {
        Object sessionIdValue = request.parameters().get("sessionId");
        if (!(sessionIdValue instanceof String sessionId) || sessionId.isBlank()) {
            return;
        }
        try {
            conversationSessionService.recordExecution(sessionId, request, response);
        } catch (RuntimeException ex) {
            log.warn("记录会话消息失败, sessionId={}, traceId={}, reason={}",
                    sessionId, response.traceId(), ex.getMessage());
        }
    }
}
