package cn.cyc.ai.cog.runtime.coordinator;

import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.runtime.domain.AgentRuntimeResult;
import cn.cyc.ai.cog.runtime.spi.AgentRuntime;
import cn.cyc.ai.cog.runtime.support.RuntimeContextParameters;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpanType;
import cn.cyc.ai.cog.runtime.trace.span.TraceSpanRecorder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 多 Agent 协调器：按策略委派子 Agent 并汇总结果。
 *
 * @author cyc
 */
@Component
public class MultiAgentCoordinator {

    private final ObjectProvider<AgentRuntime> agentRuntimeProvider;
    private final TraceSpanRecorder traceSpanRecorder;

    public MultiAgentCoordinator(ObjectProvider<AgentRuntime> agentRuntimeProvider,
                                 TraceSpanRecorder traceSpanRecorder) {
        this.agentRuntimeProvider = agentRuntimeProvider;
        this.traceSpanRecorder = traceSpanRecorder;
    }

    /**
     * 是否启用多 Agent 协作。
     *
     * @param context 运行时上下文
     * @return 是否启用
     */
    public static boolean isEnabled(ExecutionContext context) {
        return RuntimeContextParameters.flag(context, "multiAgentEnabled");
    }

    /**
     * 解析委派 Agent 编码列表。
     *
     * @param context 运行时上下文
     * @return 委派 Agent 编码
     */
    public static List<String> resolveDelegateCodes(ExecutionContext context) {
        return RuntimeContextParameters.stringList(context, "delegateAgentCodes");
    }

    /**
     * 解析执行策略。
     *
     * @param context 运行时上下文
     * @return 执行策略
     */
    public static ExecutionStrategy resolveStrategy(ExecutionContext context) {
        Object raw = context.request().parameters().get("executionStrategy");
        return ExecutionStrategy.from(raw == null ? null : String.valueOf(raw));
    }

    /**
     * 按策略执行子 Agent 委派。
     *
     * @param parentContext      父上下文
     * @param delegateAgentCodes 子 Agent 编码
     * @param strategy           执行策略
     * @return 委派结果
     */
    public List<DelegateAgentResult> executeDelegates(ExecutionContext parentContext,
                                                     List<String> delegateAgentCodes,
                                                     ExecutionStrategy strategy) {
        if (delegateAgentCodes == null || delegateAgentCodes.isEmpty()) {
            return List.of();
        }
        List<String> selectedCodes = selectDelegateCodes(delegateAgentCodes, strategy);
        AgentRuntime agentRuntime = agentRuntimeProvider.getObject();
        List<DelegateAgentResult> results = new ArrayList<>();
        for (String delegateAgentCode : selectedCodes) {
            TraceSpanRecorder.SpanScope delegateSpan = traceSpanRecorder.open(
                    parentContext.traceId(),
                    TraceSpanType.DELEGATE,
                    delegateAgentCode,
                    Map.of("strategy", strategy.name()));
            try {
                CapabilityDefinition delegateCapability = withBoundAgent(
                        parentContext.capability(),
                        delegateAgentCode);
                ExecutionContext delegateContext = new ExecutionContext(
                        parentContext.traceId(),
                        parentContext.request(),
                        delegateCapability,
                        null,
                        null,
                        null,
                        Map.of("parentAgentCode", parentContext.agent().agentCode()));
                AgentRuntimeResult delegateResult = agentRuntime.execute(delegateContext);
                ExecutionResult executionResult = delegateResult.result();
                Map<String, Object> summary = summarizeOutput(executionResult.output());
                traceSpanRecorder.succeed(delegateSpan, Map.of("status", executionResult.status()));
                results.add(new DelegateAgentResult(delegateAgentCode, executionResult.status(), summary));
            } catch (RuntimeException ex) {
                traceSpanRecorder.fail(delegateSpan, ex, Map.of("failureReason", ex.getMessage()));
                throw ex;
            }
        }
        return results;
    }

    private List<String> selectDelegateCodes(List<String> delegateAgentCodes, ExecutionStrategy strategy) {
        if (strategy == ExecutionStrategy.COST_FIRST) {
            return List.of(delegateAgentCodes.get(0));
        }
        return delegateAgentCodes;
    }

    private Map<String, Object> summarizeOutput(Map<String, Object> output) {
        Map<String, Object> summary = new LinkedHashMap<>();
        if (output == null) {
            return summary;
        }
        summary.put("executorType", output.get("executorType"));
        summary.put("businessOutput", output.get("businessOutput"));
        summary.put("mock", output.get("mock"));
        return summary;
    }

    static CapabilityDefinition withBoundAgent(CapabilityDefinition capability, String boundAgentCode) {
        return new CapabilityDefinition(
                capability.capabilityCode(),
                capability.capabilityName(),
                capability.capabilityDesc(),
                capability.inputSchema(),
                capability.outputSchema(),
                capability.parameterConstraints(),
                capability.executeMode(),
                boundAgentCode,
                capability.riskLevel(),
                capability.needHumanConfirm(),
                capability.status(),
                capability.version(),
                capability.publishedAt(),
                capability.lifecycleStatus()
        );
    }
}
