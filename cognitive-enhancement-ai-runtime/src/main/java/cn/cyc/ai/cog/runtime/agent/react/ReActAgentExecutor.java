package cn.cyc.ai.cog.runtime.agent.react;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.runtime.api.ChatMessage;
import cn.cyc.ai.cog.runtime.api.LlmConversationRequest;
import cn.cyc.ai.cog.runtime.api.LlmConversationResult;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.api.LlmToolCall;
import cn.cyc.ai.cog.runtime.api.ModelGovernanceResolution;
import cn.cyc.ai.cog.runtime.api.ToolInvocationResult;
import cn.cyc.ai.cog.runtime.budget.TaskBudgetController;
import cn.cyc.ai.cog.runtime.config.ReActProperties;
import cn.cyc.ai.cog.runtime.model.governance.DefaultModelGovernance;
import cn.cyc.ai.cog.runtime.session.service.ConversationContext;
import cn.cyc.ai.cog.runtime.session.service.RuntimeConversationContextManager;
import cn.cyc.ai.cog.runtime.spi.LlmGateway;
import cn.cyc.ai.cog.runtime.tool.spi.ToolRuntime;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpanType;
import cn.cyc.ai.cog.runtime.trace.span.TraceSpanRecorder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ReAct 多轮 Tool 循环执行器（对齐 zcloud ReActAgentExecutionStrategy）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class ReActAgentExecutor {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(ReActAgentExecutor.class);
    private static final String REACT_SYSTEM_PROMPT = """
            你是任务型 Agent。可用 tools 时按 ReAct 模式：分析需求 → 调用 tool → 根据 observation 继续推理，直到能给出最终答案。
            不需要 tool 时直接回答。仅调用已授权 tools，arguments 须为合法 JSON。
            """;

    /** llm网关。 */
    private final LlmGateway llmGateway;
    /** 工具运行时。 */
    private final ToolRuntime toolRuntime;
    /** 工具Definition仓储。 */
    private final ToolDefinitionRepository toolDefinitionRepository;
    /** reActProperties。 */
    private final ReActProperties reActProperties;
    /** 链路SpanRecorder。 */
    private final TraceSpanRecorder traceSpanRecorder;
    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;
    /** conversation上下文Manager。 */
    private final RuntimeConversationContextManager conversationContextManager;
    /** taskBudget控制器。 */
    private final TaskBudgetController taskBudgetController;
    /** 模型Governance。 */
    private final DefaultModelGovernance modelGovernance;

    /**
     * 创建ReActAgentExecutor。
     */
    public ReActAgentExecutor(LlmGateway llmGateway,
                              ToolRuntime toolRuntime,
                              ToolDefinitionRepository toolDefinitionRepository,
                              ReActProperties reActProperties,
                              TraceSpanRecorder traceSpanRecorder,
                              ObjectMapper objectMapper,
                              RuntimeConversationContextManager conversationContextManager,
                              TaskBudgetController taskBudgetController,
                              DefaultModelGovernance modelGovernance) {
        this.llmGateway = llmGateway;
        this.toolRuntime = toolRuntime;
        this.toolDefinitionRepository = toolDefinitionRepository;
        this.reActProperties = reActProperties;
        this.traceSpanRecorder = traceSpanRecorder;
        this.objectMapper = objectMapper;
        this.conversationContextManager = conversationContextManager;
        this.taskBudgetController = taskBudgetController;
        this.modelGovernance = modelGovernance;
    }

    /**
     * 执行操作。
     * @return 执行结果
     */
    public ExecutionResult execute(ExecutionContext context,
                                   ModelGovernanceResolution modelResolution,
                                   Object promptInput,
                                   List<String> allowedToolCodes,
                                   ConversationContext conversationContext) {
        ModelDefinition model = modelResolution.resolvedModel();
        int maxIterations = resolveMaxIterations(context);
        List<ToolDefinition> tools = loadTools(allowedToolCodes);
        Set<String> allowed = allowedToolCodes.stream().collect(Collectors.toSet());

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.system(REACT_SYSTEM_PROMPT));
        messages.add(ChatMessage.user(String.valueOf(promptInput)));
        messages = new ArrayList<>(conversationContextManager.augmentMessages(messages, conversationContext));

        List<Map<String, Object>> steps = new ArrayList<>();
        LlmConversationResult lastResult = null;

        for (int iteration = 1; iteration <= maxIterations; iteration++) {
            int currentIteration = iteration;
            boolean llmResponseReceived = false;
            TraceSpanRecorder.SpanScope iterationSpan = traceSpanRecorder.open(
                    context.traceId(),
                    TraceSpanType.LLM,
                    "react-iteration-" + currentIteration,
                    Map.of("modelCode", model.modelCode(), "iteration", currentIteration));
            try {
                LlmConversationRequest request = new LlmConversationRequest(
                        List.copyOf(messages),
                        tools,
                        context.request().parameters(),
                        model.timeoutMs()
                );
                try {
                    lastResult = llmGateway.chat(context, model, request);
                    taskBudgetController.chargeLlm(toInvocationResult(context, model, lastResult));
                    llmResponseReceived = true;
                } catch (RuntimeException ex) {
                    modelGovernance.recordFailure(model.modelCode());
                    throw ex;
                }
                Map<String, Object> spanAttributes = Map.of(
                        "toolCallCount", toolCallCount(lastResult),
                        "finishReason", lastResult.finishReason());

                if (!lastResult.hasToolCalls()) {
                    validateFinalAnswer(lastResult);
                    modelGovernance.recordSuccess(model.modelCode());
                    traceSpanRecorder.succeed(iterationSpan, spanAttributes);
                    return buildSuccessResult(context, lastResult, steps, modelResolution);
                }

                authorizeToolCalls(lastResult.toolCalls(), allowed);
                List<Map<String, Object>> observations = executeToolCalls(context, lastResult.toolCalls());
                modelGovernance.recordSuccess(model.modelCode());
                traceSpanRecorder.succeed(iterationSpan, spanAttributes);
                steps.add(Map.of(
                        "iteration", currentIteration,
                        "toolCalls", lastResult.toolCalls(),
                        "observations", observations
                ));
                messages.add(ChatMessage.assistant(lastResult.content(), lastResult.toolCalls()));
                appendToolMessages(messages, lastResult.toolCalls(), observations);
            } catch (RuntimeException ex) {
                if (llmResponseReceived) {
                    modelGovernance.recordFailure(model.modelCode());
                }
                traceSpanRecorder.fail(iterationSpan, ex, null);
                throw ex;
            }
        }
        throw new BusinessException("CONFLICT",
                "ReAct 超过最大迭代次数: " + maxIterations);
    }

    /**
     * 执行resolveMaxIterations。
     *
     * @param context 上下文
     * @return 执行结果
     */
    private int resolveMaxIterations(ExecutionContext context) {
        Object fromRequest = context.request().parameters().get("reactMaxIterations");
        if (fromRequest instanceof Number number) {
            return Math.max(1, number.intValue());
        }
        return Math.max(1, reActProperties.getMaxIterations());
    }

    /**
     * 执行loadTools。
     *
     * @param toolCodes 工具Codes
     * @return 执行结果
     */
    private List<ToolDefinition> loadTools(List<String> toolCodes) {
        List<ToolDefinition> tools = new ArrayList<>();
        for (String toolCode : toolCodes) {
            tools.add(toolDefinitionRepository.findByCode(toolCode)
                    .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到 Tool: " + toolCode)));
        }
        return tools;
    }

    /**
     * 执行authorize工具Calls。
     *
     * @param toolCalls 工具Calls
     * @param allowed allowed
     */
    private void authorizeToolCalls(List<LlmToolCall> toolCalls, Set<String> allowed) {
        for (LlmToolCall toolCall : toolCalls) {
            if (!allowed.contains(toolCall.name())) {
                throw new BusinessException("CONFLICT", "ReAct 调用了未授权 Tool: " + toolCall.name());
            }
        }
    }

    /**
     * 转换为olCall数量。
     *
     * @param result 结果
     * @return 转换结果
     */
    private int toolCallCount(LlmConversationResult result) {
        return result.toolCalls() == null ? 0 : result.toolCalls().size();
    }

    /**
     * 校验参数。
     *
     * @param result 结果
     */
    private void validateFinalAnswer(LlmConversationResult result) {
        if (!StringUtils.hasText(result.content())) {
            throw new BusinessException("CONFLICT", "ReAct LLM 未返回最终回答");
        }
    }

    private List<Map<String, Object>> executeToolCalls(ExecutionContext context, List<LlmToolCall> toolCalls) {
        List<Map<String, Object>> observations = new ArrayList<>();
        for (LlmToolCall toolCall : toolCalls) {
            Object input = parseToolArguments(toolCall.arguments());
            ToolInvocationResult result;
            try {
                result = toolRuntime.invoke(context, toolCall.name(), input);
                taskBudgetController.chargeTool();
            } catch (RuntimeException ex) {
                observations.add(Map.of(
                        "toolCode", toolCall.name(),
                        "success", false,
                        "error", ex.getMessage()
                ));
                continue;
            }
            observations.add(Map.of(
                    "toolCode", toolCall.name(),
                    "success", true,
                    "output", result.toolPayload(),
                    "mock", result.mock()
            ));
        }
        return observations;
    }

    /**
     * 转换为Invocation结果。
     * @return 转换结果
     */
    private LlmInvocationResult toInvocationResult(ExecutionContext context,
                                                   ModelDefinition model,
                                                   LlmConversationResult result) {
        return new LlmInvocationResult(
                "REACT",
                model.providerCode(),
                model.modelCode(),
                context.prompt() == null ? null : context.prompt().promptCode(),
                null,
                result.content(),
                context.request().parameters(),
                result.inputTokenCount(),
                result.outputTokenCount(),
                result.totalTokenCount(),
                result.latencyMs(),
                result.mock()
        );
    }

    /**
     * 执行parse工具Arguments。
     *
     * @param argumentsJson argumentsJSON
     * @return 执行结果
     */
    private Object parseToolArguments(String argumentsJson) {
        if (!StringUtils.hasText(argumentsJson)) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(argumentsJson, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception exception) {
            throw new BusinessException("INVALID_ARGUMENT", "Tool arguments 不是合法 JSON: " + argumentsJson);
        }
    }

    /**
     * 执行append工具Messages。
     */
    private void appendToolMessages(List<ChatMessage> messages,
                                    List<LlmToolCall> toolCalls,
                                    List<Map<String, Object>> observations) {
        for (int index = 0; index < toolCalls.size(); index++) {
            LlmToolCall toolCall = toolCalls.get(index);
            Map<String, Object> observation = observations.get(index);
            String content;
            try {
                content = objectMapper.writeValueAsString(observation);
            } catch (Exception exception) {
                content = String.valueOf(observation);
            }
            messages.add(ChatMessage.tool(toolCall.id(), toolCall.name(), content));
        }
    }

    /**
     * 构建成功结果。
     * @return 构建结果
     */
    private ExecutionResult buildSuccessResult(ExecutionContext context,
                                               LlmConversationResult result,
                                               List<Map<String, Object>> steps,
                                               ModelGovernanceResolution modelResolution) {
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("answer", result.content());
        output.put("businessOutput", Map.of("answer", result.content()));
        output.put("executionMode", "REACT");
        output.put("executorType", "REACT");
        output.put("reactSteps", steps);
        output.put("modelCode", modelResolution.resolvedModel().modelCode());
        output.put("primaryModelCode", modelResolution.primaryModelCode());
        output.put("fallbackApplied", modelResolution.fallbackApplied());
        output.put("totalTokens", result.totalTokenCount());
        log.info("ReAct 执行完成, traceId={}, agentCode={}, iterations={}, totalTokens={}",
                context.traceId(), context.agent().agentCode(), steps.size(), result.totalTokenCount());
        return new ExecutionResult("SUCCESS", result.content(), List.of(), output);
    }
}
