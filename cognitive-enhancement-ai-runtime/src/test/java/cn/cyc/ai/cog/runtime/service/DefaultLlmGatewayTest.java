package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.api.ChatMessage;
import cn.cyc.ai.cog.runtime.api.LlmConversationRequest;
import cn.cyc.ai.cog.runtime.api.LlmInvocationRequest;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.spi.LlmProviderHandler;
import cn.cyc.ai.cog.runtime.trace.repository.InMemoryTraceSpanRepository;
import cn.cyc.ai.cog.runtime.trace.span.TraceSpanRecorder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 默认 LLM Gateway 测试。
 *
 * @author cyc
 */
class DefaultLlmGatewayTest {

    @Test
    void shouldRejectUnsupportedProviderForReactChat() {
        LlmProviderHandler providerHandler = mock(LlmProviderHandler.class);
        ModelDefinition model = new ModelDefinition(
                "custom-provider",
                "Custom Provider",
                "CUSTOM_RPC",
                "custom-model",
                "Custom Model",
                "CHAT",
                "https://custom.local/llm",
                "sk-test",
                30_000,
                1,
                CommonStatus.ENABLED,
                1,
                null
        );
        when(providerHandler.supports(model)).thenReturn(false);
        DefaultLlmGateway gateway = new DefaultLlmGateway(
                List.of(providerHandler),
                new TraceSpanRecorder(new InMemoryTraceSpanRepository(), List.of())
        );
        LlmConversationRequest request = new LlmConversationRequest(
                List.of(ChatMessage.user("hello")),
                List.of(),
                Map.of(),
                30_000
        );

        BusinessException exception = assertThrows(BusinessException.class,
                () -> gateway.chat(null, model, request));

        assertEquals("未找到可用的 LLM Provider 处理器: custom-provider", exception.getMessage());
    }

    @Test
    void shouldDelegateReactChatToResolvedProviderHandler() {
        ModelDefinition model = new ModelDefinition(
                "custom-provider",
                "Custom Provider",
                "CUSTOM_RPC",
                "custom-model",
                "Custom Model",
                "CHAT",
                "https://custom.local/llm",
                "sk-test",
                30_000,
                1,
                CommonStatus.ENABLED,
                1,
                null
        );
        LlmProviderHandler providerHandler = new LlmProviderHandler() {

            @Override
            public boolean supports(ModelDefinition candidate) {
                return candidate == model;
            }

            @Override
            public LlmInvocationResult generate(LlmInvocationRequest request) {
                return null;
            }
        };
        DefaultLlmGateway gateway = new DefaultLlmGateway(
                List.of(providerHandler),
                new TraceSpanRecorder(new InMemoryTraceSpanRepository(), List.of())
        );
        LlmConversationRequest request = new LlmConversationRequest(
                List.of(ChatMessage.user("hello")),
                List.of(),
                Map.of(),
                30_000
        );

        BusinessException exception = assertThrows(BusinessException.class,
                () -> gateway.chat(sampleContext(), model, request));

        assertEquals("Provider 不支持 ReAct Chat: custom-provider", exception.getMessage());
    }

    private static ExecutionContext sampleContext() {
        SchemaDefinition schema = new SchemaDefinition("object", "schema", true, Map.of(), null, List.of());
        CapabilityDefinition capability = new CapabilityDefinition(
                "capability.qa",
                "问答",
                "测试能力",
                schema,
                schema,
                Map.of(),
                ExecutionMode.SYNC,
                "agent.qa",
                RiskLevel.LOW,
                false,
                CommonStatus.ENABLED
        );
        return new ExecutionContext(
                "trace-llm-gateway",
                new CapabilityExecuteRequest("capability.qa", Map.of("question", "hello"), Map.of()),
                capability,
                null,
                null,
                List.of(),
                Map.of()
        );
    }
}
