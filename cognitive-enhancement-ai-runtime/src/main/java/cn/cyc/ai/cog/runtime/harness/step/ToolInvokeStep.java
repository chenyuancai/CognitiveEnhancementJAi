package cn.cyc.ai.cog.runtime.harness.step;

import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.api.ToolInvocationResult;
import cn.cyc.ai.cog.runtime.tool.spi.ToolRuntime;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessScenario;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStepResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tool 调用验证步骤，验证 Tool 能被正确调用并返回结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class ToolInvokeStep implements HarnessStep {

    /** 工具运行时。 */
    private final ToolRuntime toolRuntime;
    /** 工具仓储。 */
    private final ToolDefinitionRepository toolRepository;
    /** 能力仓储。 */
    private final CapabilityDefinitionRepository capabilityRepository;
    /** 智能体仓储。 */
    private final AgentDefinitionRepository agentRepository;

    /**
     * 创建ToolInvokeStep。
     */
    public ToolInvokeStep(ToolRuntime toolRuntime,
                          ToolDefinitionRepository toolRepository,
                          CapabilityDefinitionRepository capabilityRepository,
                          AgentDefinitionRepository agentRepository) {
        this.toolRuntime = toolRuntime;
        this.toolRepository = toolRepository;
        this.capabilityRepository = capabilityRepository;
        this.agentRepository = agentRepository;
    }

    /**
     * 执行step编码。
     * @return 执行结果
     */
    @Override
    public String stepCode() {
        return "TOOL_INVOKE";
    }

    /**
     * 执行step名称。
     * @return 执行结果
     */
    @Override
    public String stepName() {
        return "Tool 调用验证";
    }

    /**
     * 执行描述。
     * @return 执行结果
     */
    @Override
    public String description() {
        return "验证 Tool 能被正确调用并返回结果";
    }

    /**
     * 执行操作。
     *
     * @param ctx ctx
     * @return 执行结果
     */
    @Override
    public HarnessStepResult run(HarnessContext ctx) {
        HarnessScenario scenario = ctx.scenario();
        if (scenario == null || scenario.toolCodes() == null || scenario.toolCodes().isEmpty()) {
            return new HarnessStepResult(
                    stepCode(), stepName(), true, 0,
                    "未配置 Tool，跳过调用", Map.of("skipped", true)
            );
        }

        List<Map<String, Object>> invokedTools = new ArrayList<>();
        long totalLatency = 0;
        boolean allPassed = true;

        CapabilityDefinition capability = null;
        AgentDefinition agent = null;
        if (scenario.capabilityCode() != null) {
            capability = capabilityRepository.findByCode(scenario.capabilityCode()).orElse(null);
        }
        if (scenario.agentCode() != null) {
            agent = agentRepository.findByCode(scenario.agentCode()).orElse(null);
        }
        CapabilityExecuteRequest request = new CapabilityExecuteRequest(
                scenario.capabilityCode() != null ? scenario.capabilityCode() : "",
                scenario.inputParams() != null ? scenario.inputParams() : Map.of(),
                Map.of()
        );

        for (String toolCode : scenario.toolCodes()) {
            ToolDefinition toolDef = toolRepository.findByCode(toolCode).orElse(null);
            long start = System.currentTimeMillis();
            try {
                ExecutionContext execContext = new ExecutionContext(
                        ctx.traceId(), request, capability, agent, null, null, Map.of()
                );
                ToolInvocationResult result = toolRuntime.invoke(execContext, toolCode, scenario.inputParams());
                long latencyMs = System.currentTimeMillis() - start;
                totalLatency += latencyMs;

                Map<String, Object> info = new LinkedHashMap<>();
                info.put("code", toolCode);
                info.put("name", toolDef != null ? toolDef.toolName() : toolCode);
                info.put("protocol", toolDef != null ? toolDef.protocolType().name() : "UNKNOWN");
                info.put("latencyMs", latencyMs);
                info.put("success", true);
                invokedTools.add(info);
            } catch (Exception ex) {
                long latencyMs = System.currentTimeMillis() - start;
                totalLatency += latencyMs;
                allPassed = false;

                Map<String, Object> info = new LinkedHashMap<>();
                info.put("code", toolCode);
                info.put("name", toolDef != null ? toolDef.toolName() : toolCode);
                info.put("latencyMs", latencyMs);
                info.put("success", false);
                info.put("error", ex.getMessage());
                invokedTools.add(info);
            }
        }

        String message = allPassed
                ? "Tool 调用成功"
                : "部分 Tool 调用失败";

        return new HarnessStepResult(
                stepCode(), stepName(), allPassed, totalLatency, message,
                Map.of("invokedTools", invokedTools, "overallToolLatencyMs", totalLatency)
        );
    }
}
