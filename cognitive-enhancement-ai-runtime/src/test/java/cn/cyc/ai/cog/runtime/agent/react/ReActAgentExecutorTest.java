package cn.cyc.ai.cog.runtime.agent.react;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.tool.RetryPolicy;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import cn.cyc.ai.cog.runtime.api.ChatMessage;
import cn.cyc.ai.cog.runtime.api.LlmConversationRequest;
import cn.cyc.ai.cog.runtime.api.LlmConversationResult;
import cn.cyc.ai.cog.runtime.api.LlmToolCall;
import cn.cyc.ai.cog.runtime.api.ModelGovernanceResolution;
import cn.cyc.ai.cog.runtime.api.ToolInvocationResult;
import cn.cyc.ai.cog.runtime.budget.TaskBudgetController;
import cn.cyc.ai.cog.runtime.config.ReActProperties;
import cn.cyc.ai.cog.runtime.model.governance.DefaultModelGovernance;
import cn.cyc.ai.cog.runtime.model.governance.ModelCircuitBreakerState;
import cn.cyc.ai.cog.runtime.session.service.ConversationContext;
import cn.cyc.ai.cog.runtime.session.service.RuntimeConversationContextManager;
import cn.cyc.ai.cog.runtime.spi.LlmGateway;
import cn.cyc.ai.cog.runtime.tool.spi.ToolRuntime;
import cn.cyc.ai.cog.runtime.trace.repository.InMemoryTraceSpanRepository;
import cn.cyc.ai.cog.runtime.trace.span.TraceSpanRecorder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ReActAgentExecutorTest {

    private LlmGateway llmGateway;
    private ToolRuntime toolRuntime;
    private ToolDefinitionRepository toolDefinitionRepository;
    private RuntimeConversationContextManager conversationContextManager;
    private TaskBudgetController taskBudgetController;
    private DefaultModelGovernance modelGovernance;
    private ReActAgentExecutor executor;

    @BeforeEach
    void setUp() {
        llmGateway = mock(LlmGateway.class);
        toolRuntime = mock(ToolRuntime.class);
        toolDefinitionRepository = mock(ToolDefinitionRepository.class);
        conversationContextManager = mock(RuntimeConversationContextManager.class);
        taskBudgetController = mock(TaskBudgetController.class);
        modelGovernance = mock(DefaultModelGovernance.class);

        ReActProperties properties = new ReActProperties();
        properties.setMaxIterations(3);
        executor = new ReActAgentExecutor(
                llmGateway,
                toolRuntime,
                toolDefinitionRepository,
                properties,
                new TraceSpanRecorder(new InMemoryTraceSpanRepository(), List.of()),
                new ObjectMapper(),
                conversationContextManager,
                taskBudgetController,
                modelGovernance
        );

        when(toolDefinitionRepository.findByCode("tool.search")).thenReturn(Optional.of(sampleTool()));
        when(conversationContextManager.augmentMessages(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldCompleteAfterToolCallAndFinalAnswer() {
        when(llmGateway.chat(any(), any(), any()))
                .thenReturn(toolCallResult())
                .thenReturn(finalAnswerResult());
        when(toolRuntime.invoke(any(), eq("tool.search"), any()))
                .thenReturn(new ToolInvocationResult(
                        "TOOL", "tool.search", "HTTP", "public", "LOW",
                        Map.of("query", "weather"), Map.of(), Map.of("result", "sunny"), false));

        ExecutionResult result = executor.execute(
                sampleContext(Map.of()),
                sampleResolution(),
                "今天天气如何？",
                List.of("tool.search"),
                ConversationContext.disabled());

        assertEquals("SUCCESS", result.status());
        assertEquals("北京晴，25°C。", result.message());
        @SuppressWarnings("unchecked")
        Map<String, Object> output = (Map<String, Object>) result.output();
        assertEquals("REACT", output.get("executionMode"));
        verify(llmGateway, times(2)).chat(any(), any(), any());
    }

    @Test
    void shouldChargeBudgetAndRecordModelSuccessForReactIterationsAndTools() {
        when(llmGateway.chat(any(), any(), any()))
                .thenReturn(toolCallResult())
                .thenReturn(finalAnswerResult());
        when(toolRuntime.invoke(any(), eq("tool.search"), any()))
                .thenReturn(new ToolInvocationResult(
                        "TOOL", "tool.search", "HTTP", "public", "LOW",
                        Map.of("query", "weather"), Map.of(), Map.of("result", "sunny"), false));

        executor.execute(
                sampleContext(Map.of()),
                sampleResolution(),
                "question",
                List.of("tool.search"),
                ConversationContext.disabled());

        verify(taskBudgetController, times(2)).chargeLlm(any());
        verify(taskBudgetController).chargeTool();
        verify(modelGovernance, times(2)).recordSuccess("qwen-plus");
    }

    @Test
    void shouldRecordModelFailureWhenReactLlmCallFails() {
        when(llmGateway.chat(any(), any(), any()))
                .thenThrow(new BusinessException("CONFLICT", "模型调用失败"));

        assertThrows(BusinessException.class, () -> executor.execute(
                sampleContext(Map.of()),
                sampleResolution(),
                "question",
                List.of("tool.search"),
                ConversationContext.disabled()));

        verify(modelGovernance).recordFailure("qwen-plus");
    }

    @Test
    void shouldRejectUnauthorizedToolCall() {
        when(llmGateway.chat(any(), any(), any()))
                .thenReturn(new LlmConversationResult(
                        null,
                        List.of(new LlmToolCall("call-1", "tool.forbidden", "{}")),
                        "tool_calls",
                        10, 5, 15, 100, false));

        BusinessException exception = assertThrows(BusinessException.class, () -> executor.execute(
                sampleContext(Map.of()),
                sampleResolution(),
                "question",
                List.of("tool.search"),
                ConversationContext.disabled()));

        assertEquals("CONFLICT", exception.getSemanticCode());
    }

    @Test
    void shouldRejectInvalidToolArgumentsJson() {
        when(llmGateway.chat(any(), any(), any()))
                .thenReturn(new LlmConversationResult(
                        null,
                        List.of(new LlmToolCall("call-1", "tool.search", "{bad-json")),
                        "tool_calls",
                        10, 5, 15, 100, false));

        BusinessException exception = assertThrows(BusinessException.class, () -> executor.execute(
                sampleContext(Map.of()),
                sampleResolution(),
                "question",
                List.of("tool.search"),
                ConversationContext.disabled()));

        assertEquals("INVALID_ARGUMENT", exception.getSemanticCode());
        verifyNoInteractions(toolRuntime);
    }

    @Test
    void shouldContinueAfterToolInvocationFailureWithObservation() {
        when(llmGateway.chat(any(), any(), any()))
                .thenReturn(toolCallResult())
                .thenReturn(finalAnswerResult());
        when(toolRuntime.invoke(any(), eq("tool.search"), any()))
                .thenThrow(new BusinessException("CONFLICT", "工具执行失败"));

        ExecutionResult result = executor.execute(
                sampleContext(Map.of()),
                sampleResolution(),
                "question",
                List.of("tool.search"),
                ConversationContext.disabled());

        assertEquals("SUCCESS", result.status());
        ArgumentCaptor<LlmConversationRequest> requestCaptor = ArgumentCaptor.forClass(LlmConversationRequest.class);
        verify(llmGateway, times(2)).chat(any(), any(), requestCaptor.capture());
        List<ChatMessage> secondRoundMessages = requestCaptor.getAllValues().get(1).messages();
        ChatMessage toolMessage = secondRoundMessages.stream()
                .filter(message -> "tool".equals(message.role()))
                .findFirst()
                .orElseThrow();
        assertTrue(toolMessage.content().contains("\"success\":false"));
        assertTrue(toolMessage.content().contains("工具执行失败"));
    }

    @Test
    void shouldFailWhenMaxIterationsExceeded() {
        when(llmGateway.chat(any(), any(), any())).thenReturn(toolCallResult());
        when(toolRuntime.invoke(any(), eq("tool.search"), any()))
                .thenReturn(new ToolInvocationResult(
                        "TOOL", "tool.search", "HTTP", "public", "LOW",
                        Map.of(), Map.of(), Map.of("result", "ok"), false));

        BusinessException exception = assertThrows(BusinessException.class, () -> executor.execute(
                sampleContext(Map.of()),
                sampleResolution(),
                "loop",
                List.of("tool.search"),
                ConversationContext.disabled()));

        assertEquals("CONFLICT", exception.getSemanticCode());
        verify(llmGateway, times(3)).chat(any(), any(), any());
    }

    @Test
    void shouldHonorReactMaxIterationsFromRequestParameters() {
        when(llmGateway.chat(any(), any(), any())).thenReturn(toolCallResult());
        when(toolRuntime.invoke(any(), eq("tool.search"), any()))
                .thenReturn(new ToolInvocationResult(
                        "TOOL", "tool.search", "HTTP", "public", "LOW",
                        Map.of(), Map.of(), Map.of("result", "ok"), false));

        assertThrows(BusinessException.class, () -> executor.execute(
                sampleContext(Map.of("reactMaxIterations", 1)),
                sampleResolution(),
                "loop",
                List.of("tool.search"),
                ConversationContext.disabled()));

        verify(llmGateway, times(1)).chat(any(), any(), any());
    }

    private LlmConversationResult toolCallResult() {
        return new LlmConversationResult(
                null,
                List.of(new LlmToolCall("call-1", "tool.search", "{\"query\":\"weather\"}")),
                "tool_calls",
                10, 0, 10, 50, false);
    }

    private LlmConversationResult finalAnswerResult() {
        return new LlmConversationResult(
                "北京晴，25°C。",
                List.of(),
                "stop",
                20, 10, 30, 80, false);
    }

    private ExecutionContext sampleContext(Map<String, Object> parameters) {
        SchemaDefinition schema = new SchemaDefinition("object", "schema", true, Map.of(), null, List.of());
        CapabilityDefinition capability = new CapabilityDefinition(
                "capability.qa",
                "问答",
                "测试",
                schema,
                schema,
                Map.of(),
                ExecutionMode.SYNC,
                "agent.qa",
                RiskLevel.LOW,
                false,
                CommonStatus.ENABLED
        );
        AgentDefinition agent = new AgentDefinition(
                "agent.qa",
                "QA Agent",
                "role",
                "goal",
                "qwen-plus",
                4,
                BigDecimal.ONE,
                20000,
                List.of("skill.qa"),
                Map.of(),
                CommonStatus.ENABLED
        );
        return new ExecutionContext(
                "trace-react",
                new CapabilityExecuteRequest("capability.qa", Map.of("question", "hello"), parameters),
                capability,
                agent,
                null,
                List.of(),
                Map.of()
        );
    }

    private ModelGovernanceResolution sampleResolution() {
        ModelDefinition model = new ModelDefinition(
                "bailian",
                "百炼",
                "qwen-plus",
                "Qwen Plus",
                "CHAT",
                "https://dashscope.aliyuncs.com/compatible-mode/v1",
                "sk-test",
                30000,
                1,
                CommonStatus.ENABLED,
                10,
                null
        );
        return new ModelGovernanceResolution(
                model,
                "qwen-plus",
                "qwen-plus",
                false,
                ModelCircuitBreakerState.CLOSED
        );
    }

    private ToolDefinition sampleTool() {
        SchemaDefinition schema = new SchemaDefinition("object", "schema", true, Map.of(), null, List.of());
        return new ToolDefinition(
                "tool.search",
                "Search",
                ToolProtocolType.HTTP,
                schema,
                schema,
                "public",
                RiskLevel.LOW,
                5000,
                new RetryPolicy(1),
                "http://localhost/search",
                CommonStatus.ENABLED
        );
    }
}
