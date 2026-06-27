package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.harness.SkillLoader;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.runtime.api.ModelGovernanceResolution;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.api.ToolInvocationResult;
import cn.cyc.ai.cog.runtime.domain.AgentRuntimeResult;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.spi.AgentRuntime;
import cn.cyc.ai.cog.runtime.spi.ExecutionParameterValidator;
import cn.cyc.ai.cog.runtime.spi.LlmGateway;
import cn.cyc.ai.cog.runtime.spi.OutputSchemaValidator;
import cn.cyc.ai.cog.runtime.budget.TaskBudgetController;
import cn.cyc.ai.cog.runtime.coordinator.DelegateAgentResult;
import cn.cyc.ai.cog.runtime.coordinator.ExecutionStrategy;
import cn.cyc.ai.cog.runtime.coordinator.MultiAgentCoordinator;
import cn.cyc.ai.cog.runtime.model.governance.DefaultModelGovernance;
import cn.cyc.ai.cog.runtime.coordinator.MultiAgentResultMerger;
import cn.cyc.ai.cog.runtime.agent.react.ReActAgentExecutor;
import cn.cyc.ai.cog.runtime.config.ReActProperties;
import cn.cyc.ai.cog.runtime.planner.PlanDrivenToolSelector;
import cn.cyc.ai.cog.runtime.planner.TaskPlanProgress;
import cn.cyc.ai.cog.runtime.planner.TaskPlan;
import cn.cyc.ai.cog.runtime.planner.TaskPlanner;
import cn.cyc.ai.cog.runtime.reflection.ExecutionLoopGuard;
import cn.cyc.ai.cog.runtime.reflection.ExecutionReflector;
import cn.cyc.ai.cog.runtime.reflection.ReflectionOutcome;
import cn.cyc.ai.cog.runtime.session.service.ConversationContext;
import cn.cyc.ai.cog.runtime.session.service.RuntimeConversationContextManager;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpanType;
import cn.cyc.ai.cog.runtime.trace.span.TraceSpanRecorder;
import cn.cyc.ai.cog.runtime.spi.PromptResolver;
import cn.cyc.ai.cog.runtime.tool.spi.ToolRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 默认 AgentRuntime，实现一期最小装载与 mock 执行。
 *
 * @author cyc
 */
@Service
public class DefaultAgentRuntime implements AgentRuntime {

    /**
     * 运行时日志。
     */
    private static final Logger log = LoggerFactory.getLogger(DefaultAgentRuntime.class);

    /**
     * Agent 定义仓储。
     */
    private final AgentDefinitionRepository agentDefinitionRepository;

    /**
     * 技能装载器。
     */
    private final SkillLoader skillLoader;

    /**
     * 模型定义仓储。
     */
    private final ModelDefinitionRepository modelDefinitionRepository;

    /**
     * 执行参数校验器。
     */
    private final ExecutionParameterValidator executionParameterValidator;

    /**
     * Tool 运行时。
     */
    private final ToolRuntime toolRuntime;

    /**
     * Prompt 解析器。
     */
    private final PromptResolver promptResolver;

    /**
     * LLM 调用网关。
     */
    private final LlmGateway llmGateway;

    /**
     * 模型治理。
     */
    private final DefaultModelGovernance modelGovernance;

    private final TraceSpanRecorder traceSpanRecorder;

    /**
     * 输出 Schema 校验器。
     */
    private final OutputSchemaValidator outputSchemaValidator;

    /**
     * 任务规划器。
     */
    private final TaskPlanner taskPlanner;

    /**
     * 多 Agent 协调器。
     */
    private final MultiAgentCoordinator multiAgentCoordinator;

    /**
     * 执行循环防护。
     */
    private final ExecutionLoopGuard executionLoopGuard;

    /**
     * 任务预算控制器。
     */
    private final TaskBudgetController taskBudgetController;

    /**
     * 自反思器。
     */
    private final ExecutionReflector executionReflector;

    /**
     * 多 Agent 结果合并器。
     */
    private final MultiAgentResultMerger multiAgentResultMerger;

    /**
     * 计划驱动 Tool 选择器。
     */
    private final PlanDrivenToolSelector planDrivenToolSelector;

    private final ReActAgentExecutor reActAgentExecutor;

    private final ReActProperties reActProperties;

    private final RuntimeConversationContextManager conversationContextManager;

    /**
     * 构造默认 AgentRuntime。
     *
     * @param agentDefinitionRepository Agent 定义仓储
     * @param skillLoader               Skill 装载器
     * @param modelDefinitionRepository 模型定义仓储
     * @param executionParameterValidator 执行参数校验器
     * @param toolRuntime               Tool 运行时
     * @param promptResolver            Prompt 解析器
     * @param llmGateway                LLM 调用网关
     * @param modelGovernance           模型治理
     * @param traceSpanRecorder         Trace 记录器
     * @param outputSchemaValidator     输出 Schema 校验器
     * @param taskPlanner               任务规划器
     * @param multiAgentCoordinator     多 Agent 协调器
     * @param executionLoopGuard        执行循环防护
     * @param taskBudgetController      任务预算控制器
     * @param executionReflector        自反思器
     * @param multiAgentResultMerger    多 Agent 结果合并器
     * @param planDrivenToolSelector    计划驱动 Tool 选择器
     */
    public DefaultAgentRuntime(AgentDefinitionRepository agentDefinitionRepository,
                               SkillLoader skillLoader,
                               ModelDefinitionRepository modelDefinitionRepository,
                               ExecutionParameterValidator executionParameterValidator,
                               ToolRuntime toolRuntime,
                               PromptResolver promptResolver,
                               LlmGateway llmGateway,
                               DefaultModelGovernance modelGovernance,
                               TraceSpanRecorder traceSpanRecorder,
                               OutputSchemaValidator outputSchemaValidator,
                               TaskPlanner taskPlanner,
                               MultiAgentCoordinator multiAgentCoordinator,
                               ExecutionLoopGuard executionLoopGuard,
                               TaskBudgetController taskBudgetController,
                               ExecutionReflector executionReflector,
                               MultiAgentResultMerger multiAgentResultMerger,
                               PlanDrivenToolSelector planDrivenToolSelector,
                               ReActAgentExecutor reActAgentExecutor,
                               ReActProperties reActProperties,
                               RuntimeConversationContextManager conversationContextManager) {
        this.agentDefinitionRepository = agentDefinitionRepository;
        this.skillLoader = skillLoader;
        this.modelDefinitionRepository = modelDefinitionRepository;
        this.executionParameterValidator = executionParameterValidator;
        this.toolRuntime = toolRuntime;
        this.promptResolver = promptResolver;
        this.llmGateway = llmGateway;
        this.modelGovernance = modelGovernance;
        this.traceSpanRecorder = traceSpanRecorder;
        this.outputSchemaValidator = outputSchemaValidator;
        this.taskPlanner = taskPlanner;
        this.multiAgentCoordinator = multiAgentCoordinator;
        this.executionLoopGuard = executionLoopGuard;
        this.taskBudgetController = taskBudgetController;
        this.executionReflector = executionReflector;
        this.multiAgentResultMerger = multiAgentResultMerger;
        this.planDrivenToolSelector = planDrivenToolSelector;
        this.reActAgentExecutor = reActAgentExecutor;
        this.reActProperties = reActProperties;
        this.conversationContextManager = conversationContextManager;
    }

    /**
     * 执行 Agent 主链路。
     *
     * @param context 运行时上下文
     * @return Agent 执行结果
     */
    @Override
    public AgentRuntimeResult execute(ExecutionContext context) {
        String agentCode = context.capability().boundAgentCode();
        AgentDefinition agent = agentDefinitionRepository.findByCode(agentCode)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到绑定 Agent: " + agentCode));
        if (agent.status() != CommonStatus.ENABLED) {
            throw new BusinessException("CONFLICT", "Agent 未启用: " + agentCode);
        }
        executionParameterValidator.validate(context.request(), context.capability(), agent);
        taskBudgetController.start(context, agent);
        ExecutionStrategy executionStrategy = MultiAgentCoordinator.resolveStrategy(context);

        TraceSpanRecorder.SpanScope agentSpan = traceSpanRecorder.open(
                context.traceId(),
                TraceSpanType.AGENT,
                agent.agentCode(),
                Map.of("modelCode", agent.modelCode(), "executionStrategy", executionStrategy.name()));
        try {
            List<SkillDefinition> skills = skillLoader.loadForAgent(agent);
            ModelGovernanceResolution modelResolution = modelGovernance.resolveModel(agent.modelCode(), executionStrategy);
            ModelDefinition model = modelResolution.resolvedModel();
            PromptTemplate promptTemplate = promptResolver.resolve(context);
            ExecutionContext routedContext = context.withAgentPromptAndSkills(agent, promptTemplate, skills);
            ConversationContext conversationContext = conversationContextManager.load(routedContext);
            Optional<TaskPlan> taskPlan = taskPlanner.plan(routedContext);
            log.info("AgentRuntime 已完成基础装载, traceId={}, capabilityCode={}, agentCode={}, primaryModelCode={}, modelCode={}, fallbackApplied={}, promptCode={}, skillCount={}, planning={}",
                    routedContext.traceId(),
                    routedContext.capability().capabilityCode(),
                    routedContext.agent().agentCode(),
                    modelResolution.primaryModelCode(),
                    model.modelCode(),
                    modelResolution.fallbackApplied(),
                    routedContext.prompt() == null ? "NONE" : routedContext.prompt().promptCode(),
                    skills.size(),
                    taskPlan.isPresent());

            List<String> boundToolCodes = collectToolCodes(skills);
            ExecutionResult result;
            if (boundToolCodes.isEmpty()) {
                result = buildLlmResult(routedContext, modelResolution, conversationContext);
            } else if (shouldUseReAct(routedContext)) {
                Object promptInput = routedContext.prompt() == null
                        ? routedContext.request().input()
                        : promptResolver.render(routedContext.prompt(), routedContext);
                result = reActAgentExecutor.execute(routedContext, modelResolution, promptInput, boundToolCodes, conversationContext);
            } else {
                result = buildToolThenLlmResult(routedContext, boundToolCodes, modelResolution, taskPlan, conversationContext);
            }
            result = enrichExecutionResult(routedContext, result, taskPlan, executionStrategy);
            outputSchemaValidator.validate(routedContext.capability(), result);
            traceSpanRecorder.succeed(agentSpan, Map.of("resultStatus", result.status()));
            return new AgentRuntimeResult(routedContext, result);
        } catch (RuntimeException ex) {
            traceSpanRecorder.fail(agentSpan, ex, Map.of("failureReason", ex.getMessage()));
            throw ex;
        } finally {
            executionLoopGuard.clear();
            taskBudgetController.clear();
        }
    }

    /**
     * 追加 PH5 治理输出：任务规划、多 Agent 委派与预算摘要。
     */
    private ExecutionResult enrichExecutionResult(ExecutionContext context,
                                                  ExecutionResult result,
                                                  Optional<TaskPlan> taskPlan,
                                                  ExecutionStrategy executionStrategy) {
        Map<String, Object> output = new LinkedHashMap<>(result.output());
        taskPlan.map(TaskPlanProgress::markAllDone).ifPresent(plan -> output.put("taskPlan", plan));
        taskBudgetController.remaining().ifPresent(remaining -> output.put("budgetRemaining", remaining));
        output.put("executionStrategy", executionStrategy.name());

        if (!MultiAgentCoordinator.isEnabled(context)) {
            return copyResult(result, output);
        }
        List<String> delegateAgentCodes = MultiAgentCoordinator.resolveDelegateCodes(context);
        if (delegateAgentCodes.isEmpty()) {
            return copyResult(result, output);
        }
        List<DelegateAgentResult> delegateResults = multiAgentCoordinator.executeDelegates(
                context,
                delegateAgentCodes,
                executionStrategy);
        output.put("multiAgentResults", delegateResults);
        output.put("delegateAgentCodes", delegateAgentCodes);
        Map<String, Object> mergedBusinessOutput = multiAgentResultMerger.merge(output, delegateResults, executionStrategy);
        if (mergedBusinessOutput != null) {
            output.put("mergedBusinessOutput", mergedBusinessOutput);
            output.put("businessOutput", mergedBusinessOutput);
        }
        return copyResult(result, output);
    }

    private ExecutionResult copyResult(ExecutionResult result, Map<String, Object> output) {
        return new ExecutionResult(result.status(), result.message(), result.allowedSkillCodes(), output);
    }

    /**
     * 汇总技能绑定的 Tool 编码。
     *
     * @param skills 已装载技能
     * @return 去重后的 Tool 编码列表
     */
    private List<String> collectToolCodes(List<SkillDefinition> skills) {
        return skills.stream()
                .flatMap(skill -> skill.boundToolCodes().stream())
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private boolean shouldUseReAct(ExecutionContext context) {
        Object override = context.request().parameters().get("reactEnabled");
        if (override instanceof Boolean enabled) {
            return enabled;
        }
        return reActProperties.isEnabled();
    }

    /**
     * 构建 Tool → LLM 串联执行结果：先调用 Tool 获取上下文，再由 LLM 生成最终回答。
     *
     * @param context   运行时上下文
     * @param toolCodes 可用 Tool 列表
     * @param modelResolution 模型治理解析结果
     * @return 执行结果
     */
    private ExecutionResult buildToolThenLlmResult(ExecutionContext context,
                                                   List<String> toolCodes,
                                                   ModelGovernanceResolution modelResolution,
                                                   Optional<TaskPlan> taskPlan,
                                                   ConversationContext conversationContext) {
        String selectedToolCode = planDrivenToolSelector.selectTool(toolCodes, taskPlan, context);
        executionLoopGuard.check(context.traceId(), "tool:" + selectedToolCode);
        ToolInvocationResult toolOutput = toolRuntime.invoke(context, selectedToolCode, context.request().input());
        taskBudgetController.chargeTool();
        Object promptInput = buildToolAugmentedPromptInput(context, toolOutput);
        promptInput = conversationContextManager.augmentPromptInput(context, promptInput, conversationContext);
        LlmPipelineResult llmPipeline = invokeLlmWithReflection(context, modelResolution, promptInput);
        LlmInvocationResult llmOutput = llmPipeline.llmResult();
        ModelDefinition model = modelResolution.resolvedModel();
        Map<String, Object> output = baseOutput(context);
        output.put("mock", toolOutput.mock() && llmOutput.mock());
        output.put("executorType", "TOOL_THEN_LLM");
        output.put("toolCodes", toolCodes);
        output.put("selectedToolCode", selectedToolCode);
        output.put("planDrivenToolSelection", planDrivenToolSelector.isEnabled(context));
        appendModelGovernanceOutput(output, modelResolution, model, llmOutput);
        appendReflectionOutput(output, llmPipeline.reflectionOutcome());
        output.put("invocationResult", toolOutput);
        output.put("toolResult", toolOutput);
        output.put("llmResult", llmOutput);
        output.put("businessOutput", buildLlmBusinessOutput(llmOutput));
        return new ExecutionResult(
                "TOOL_THEN_LLM",
                "能力已完成 Tool 调用并触发 LLM 生成",
                context.agent().allowedSkillCodes(),
                output
        );
    }

    /**
     * 将 Tool 结果与 Prompt/输入合并，供 LLM 二次生成使用。
     *
     * @param context    运行时上下文
     * @param toolOutput Tool 调用结果
     * @return LLM 提示词输入
     */
    private Object buildToolAugmentedPromptInput(ExecutionContext context, ToolInvocationResult toolOutput) {
        Map<String, Object> payload = new LinkedHashMap<>();
        if (context.prompt() == null) {
            payload.put("input", context.request().input());
        } else {
            payload.put("renderedPrompt", promptResolver.render(context.prompt(), context));
            payload.put("promptCode", context.prompt().promptCode());
        }
        payload.put("toolCode", toolOutput.toolCode());
        payload.put("toolResult", toolOutput.toolPayload());
        return payload;
    }

    /**
     * 构建 LLM 执行结果。
     *
     * @param context 运行时上下文
     * @param model   已校验模型定义
     * @return 执行结果
     */
    private ExecutionResult buildLlmResult(ExecutionContext context,
                                           ModelGovernanceResolution modelResolution,
                                           ConversationContext conversationContext) {
        Object promptInput = context.prompt() == null
                ? context.request().input()
                : promptResolver.render(context.prompt(), context);
        promptInput = conversationContextManager.augmentPromptInput(context, promptInput, conversationContext);
        LlmPipelineResult llmPipeline = invokeLlmWithReflection(context, modelResolution, promptInput);
        LlmInvocationResult llmOutput = llmPipeline.llmResult();
        ModelDefinition model = modelResolution.resolvedModel();
        Map<String, Object> output = baseOutput(context);
        output.put("mock", llmOutput.mock());
        output.put("executorType", llmOutput.executorType());
        appendModelGovernanceOutput(output, modelResolution, model, llmOutput);
        appendReflectionOutput(output, llmPipeline.reflectionOutcome());
        output.put("invocationResult", llmOutput);
        output.put("llmResult", llmOutput);
        output.put("businessOutput", buildLlmBusinessOutput(llmOutput));
        return new ExecutionResult(
                "LLM_GENERATED",
                "能力已完成路由并触发 LLM 调用",
                context.agent().allowedSkillCodes(),
                output
        );
    }

    /**
     * 通过模型治理执行 LLM 调用，并记录熔断成功/失败。
     */
    /**
     * 通过模型治理执行 LLM 调用，并在需要时触发自反思重试。
     */
    private LlmPipelineResult invokeLlmWithReflection(ExecutionContext context,
                                                      ModelGovernanceResolution modelResolution,
                                                      Object promptInput) {
        LlmInvocationResult initial = invokeLlmOnce(context, modelResolution, promptInput);
        ReflectionOutcome reflectionOutcome = executionReflector.reflectIfNeeded(
                context,
                promptInput,
                initial,
                retryInput -> invokeLlmOnce(context, modelResolution, retryInput));
        return new LlmPipelineResult(reflectionOutcome.result(), reflectionOutcome);
    }

    private LlmInvocationResult invokeLlmOnce(ExecutionContext context,
                                              ModelGovernanceResolution modelResolution,
                                              Object promptInput) {
        ModelDefinition model = modelResolution.resolvedModel();
        executionLoopGuard.check(context.traceId(), "llm:" + model.modelCode());
        try {
            LlmInvocationResult result = llmGateway.generate(context, model, promptInput);
            taskBudgetController.chargeLlm(result);
            modelGovernance.recordSuccess(model.modelCode());
            return result;
        } catch (RuntimeException exception) {
            modelGovernance.recordFailure(model.modelCode());
            throw exception;
        }
    }

    private void appendReflectionOutput(Map<String, Object> output, ReflectionOutcome reflectionOutcome) {
        output.put("reflectionApplied", reflectionOutcome.applied());
        output.put("reflectionRetryCount", reflectionOutcome.retryCount());
        if (reflectionOutcome.reflectionNote() != null) {
            output.put("reflectionNote", reflectionOutcome.reflectionNote());
        }
    }

    private record LlmPipelineResult(LlmInvocationResult llmResult, ReflectionOutcome reflectionOutcome) {
    }

    private void appendModelGovernanceOutput(Map<String, Object> output,
                                             ModelGovernanceResolution modelResolution,
                                             ModelDefinition model,
                                             LlmInvocationResult llmOutput) {
        output.put("modelCode", llmOutput.modelCode());
        output.put("providerCode", model.providerCode());
        output.put("primaryModelCode", modelResolution.primaryModelCode());
        output.put("fallbackApplied", modelResolution.fallbackApplied());
        output.put("circuitState", modelResolution.circuitState().name());
    }

    /**
     * 构建基础输出对象。
     *
     * @param context 运行时上下文
     * @return 基础输出
     */
    private Map<String, Object> baseOutput(ExecutionContext context) {
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("mock", true);
        output.put("route", "capability->agent->executor");
        output.put("agentCode", context.agent().agentCode());
        output.put("skillCount", context.skills().size());
        output.put("skillCodes", context.skills().stream().map(SkillDefinition::skillCode).toList());
        output.put("input", context.request().input());
        output.put("parameters", context.request().parameters());
        output.put("prompt", buildPromptPayload(context));
        return output;
    }

    /**
     * 生成 LLM 分支的标准业务输出。
     *
     * @param llmOutput LLM 调用结果
     * @return 业务输出
     */
    private Map<String, Object> buildLlmBusinessOutput(LlmInvocationResult llmOutput) {
        if (llmOutput.answer() == null) {
            return Map.of();
        }
        return Map.of("answer", llmOutput.answer());
    }

    /**
     * 生成 Tool 分支的标准业务输出。
     *
     * @param toolOutput Tool 调用结果
     * @return 业务输出
     */
    private Map<String, Object> buildToolBusinessOutput(ToolInvocationResult toolOutput) {
        if (!(toolOutput.toolPayload() instanceof Map<?, ?> payloadMap)) {
            return Map.of();
        }
        Object answer = payloadMap.get("answer");
        if (answer instanceof String answerText) {
            return Map.of("answer", answerText);
        }
        Object answerPreview = payloadMap.get("answerPreview");
        if (answerPreview instanceof String previewText) {
            return Map.of("answer", previewText);
        }
        return Map.of();
    }

    /**
     * 构建 Prompt 摘要信息。
     *
     * @param context 运行时上下文
     * @return Prompt 摘要
     */
    private Map<String, Object> buildPromptPayload(ExecutionContext context) {
        if (context.prompt() == null) {
            return Map.of("resolved", false);
        }
        return Map.of(
                "resolved", true,
                "promptCode", context.prompt().promptCode(),
                "scenarioCode", context.prompt().scenarioCode(),
                "version", context.prompt().version(),
                "renderedPrompt", promptResolver.render(context.prompt(), context)
        );
    }
}
