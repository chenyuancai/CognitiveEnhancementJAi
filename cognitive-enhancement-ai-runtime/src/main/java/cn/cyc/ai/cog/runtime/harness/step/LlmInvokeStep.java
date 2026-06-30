package cn.cyc.ai.cog.runtime.harness.step;

import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.observation.spi.UsageMeter;
import cn.cyc.ai.cog.runtime.spi.LlmGateway;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessScenario;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStepResult;
import cn.cyc.ai.cog.runtime.harness.support.HarnessImportWorkflowSupport;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * LLM 调用验证步骤，验证 LLM Gateway 能正确调用外部模型。
 * <p>默认使用百炼 qwen-plus 模型。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class LlmInvokeStep implements HarnessStep {

    /** llm网关。 */
    private final LlmGateway llmGateway;
    /** usageMeter。 */
    private final UsageMeter usageMeter;
    /** 模型仓储。 */
    private final ModelDefinitionRepository modelRepository;
    /** 能力仓储。 */
    private final CapabilityDefinitionRepository capabilityRepository;
    /** 智能体仓储。 */
    private final AgentDefinitionRepository agentRepository;

    /**
     * 创建LlmInvokeStep。
     */
    public LlmInvokeStep(LlmGateway llmGateway,
                         UsageMeter usageMeter,
                         ModelDefinitionRepository modelRepository,
                         CapabilityDefinitionRepository capabilityRepository,
                         AgentDefinitionRepository agentRepository) {
        this.llmGateway = llmGateway;
        this.usageMeter = usageMeter;
        this.modelRepository = modelRepository;
        this.capabilityRepository = capabilityRepository;
        this.agentRepository = agentRepository;
    }

    /**
     * 执行step编码。
     * @return 执行结果
     */
    @Override
    public String stepCode() {
        return "LLM_INVOKE";
    }

    /**
     * 执行step名称。
     * @return 执行结果
     */
    @Override
    public String stepName() {
        return "LLM 调用验证";
    }

    /**
     * 执行描述。
     * @return 执行结果
     */
    @Override
    public String description() {
        return "验证 LLM Gateway 能正确调用外部模型（默认百炼 qwen-plus）";
    }

    /**
     * 执行操作。
     *
     * @param ctx ctx
     * @return 执行结果
     */
    @Override
    public HarnessStepResult run(HarnessContext ctx) {
        if (HarnessImportWorkflowSupport.isImportKbFileParse(ctx.scenario())) {
            return HarnessImportWorkflowSupport.skipStep(this, "导入工作流场景，跳过 LLM 调用");
        }
        HarnessScenario scenario = ctx.scenario();
        String modelCode = scenario != null && scenario.modelCode() != null
                ? scenario.modelCode()
                : "qwen-plus";

        ModelDefinition model = modelRepository.findByCode(modelCode).orElse(null);
        if (model == null) {
            return new HarnessStepResult(
                    stepCode(), stepName(), false, 0,
                    "未找到模型: " + modelCode, Map.of("modelCode", modelCode)
            );
        }

        CapabilityDefinition capability = null;
        AgentDefinition agent = null;
        if (scenario != null && scenario.capabilityCode() != null) {
            capability = capabilityRepository.findByCode(scenario.capabilityCode()).orElse(null);
        }
        if (scenario != null && scenario.agentCode() != null) {
            agent = agentRepository.findByCode(scenario.agentCode()).orElse(null);
        }
        CapabilityExecuteRequest request = new CapabilityExecuteRequest(
                scenario != null && scenario.capabilityCode() != null ? scenario.capabilityCode() : "",
                scenario != null && scenario.inputParams() != null ? scenario.inputParams() : Map.of(),
                Map.of()
        );

        long start = System.currentTimeMillis();
        try {
            ExecutionContext execContext = new ExecutionContext(
                    ctx.traceId(), request, capability, agent, null, null, Map.of()
            );
            Object promptInput = scenario != null && scenario.inputParams() != null
                    ? scenario.inputParams()
                    : Map.of("question", "Harness 测试问题");
            LlmInvocationResult result = llmGateway.generate(execContext, model, promptInput);
            recordUsage(execContext, result);
            long latencyMs = System.currentTimeMillis() - start;

            return new HarnessStepResult(
                    stepCode(), stepName(), true, latencyMs,
                    "LLM 调用成功",
                    Map.of(
                            "modelCode", modelCode,
                            "provider", model.providerCode(),
                            "requestLatencyMs", latencyMs,
                            "responseStatus", "SUCCESS",
                            "responsePreview", truncate(result.answer(), 200),
                            "inputTokenCount", result.inputTokenCount(),
                            "outputTokenCount", result.outputTokenCount(),
                            "totalTokenCount", result.totalTokenCount()
                    )
            );
        } catch (Exception ex) {
            long latencyMs = System.currentTimeMillis() - start;
            return new HarnessStepResult(
                    stepCode(), stepName(), false, latencyMs,
                    "LLM 调用失败: " + ex.getMessage(),
                    Map.of(
                            "modelCode", modelCode,
                            "provider", model.providerCode(),
                            "requestLatencyMs", latencyMs,
                            "error", ex.getMessage()
                    )
            );
        }
    }

    /**
     * 执行recordUsage。
     *
     * @param context 上下文
     * @param llmResult llm结果
     */
    private void recordUsage(ExecutionContext context, LlmInvocationResult llmResult) {
        if (context.capability() == null || context.agent() == null) {
            return;
        }
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("executorType", llmResult.executorType());
        output.put("llmResult", llmResult);
        output.put("invocationResult", llmResult);
        ExecutionResult executionResult = new ExecutionResult(
                "LLM_GENERATED",
                "Harness LLM 步骤调用成功",
                List.of(),
                output
        );
        usageMeter.record(context, executionResult);
    }

    /**
     * 执行truncate。
     *
     * @param text text
     * @param maxLen maxLen
     * @return 执行结果
     */
    private String truncate(String text, int maxLen) {
        if (text == null) {
            return "";
        }
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }
}
