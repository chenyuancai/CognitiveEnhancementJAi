package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.api.LlmHttpRequest;
import cn.cyc.ai.cog.runtime.api.LlmHttpResponse;
import cn.cyc.ai.cog.runtime.api.LlmInvocationRequest;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.spi.LlmCredentialResolver;
import cn.cyc.ai.cog.runtime.spi.LlmHttpExecutor;
import cn.cyc.ai.cog.runtime.support.OpenAiCompatibleChatClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * OpenAI-compatible Provider 处理器测试。
 */
class OpenAiCompatibleLlmProviderHandlerTest {

    @Test
    void shouldOnlySupportRegisteredOpenAiCompatibleProviders() {
        OpenAiCompatibleLlmProviderHandler handler = new OpenAiCompatibleLlmProviderHandler(
                new DefaultLlmCredentialResolver(),
                new OpenAiCompatibleChatClient(new ObjectMapper(), mock(LlmHttpExecutor.class)));

        assertTrue(handler.supports(model("custom-compatible", "OPENAI_COMPATIBLE")));
        assertTrue(handler.supports(model("bailian", "DASHSCOPE")));
        assertTrue(handler.supports(model("openai", "openai")));
        assertFalse(handler.supports(model("unsupported-provider", "CUSTOM_RPC")));
    }

    @Test
    void generate_shouldRejectWhenApiKeyMissing() {
        OpenAiCompatibleLlmProviderHandler handler = new OpenAiCompatibleLlmProviderHandler(
                new DefaultLlmCredentialResolver(),
                new OpenAiCompatibleChatClient(new ObjectMapper(), mock(LlmHttpExecutor.class)));

        assertThrows(BusinessException.class, () -> handler.generate(invocationRequest("")));
    }

    @Test
    void generate_shouldCallHttpWhenApiKeyConfigured() {
        LlmHttpExecutor httpExecutor = mock(LlmHttpExecutor.class);
        when(httpExecutor.execute(any(LlmHttpRequest.class))).thenReturn(new LlmHttpResponse(200, """
                {
                  "choices": [
                    {
                      "message": {
                        "content": "真实模型回答"
                      }
                    }
                  ],
                  "usage": {
                    "prompt_tokens": 12,
                    "completion_tokens": 8,
                    "total_tokens": 20
                  }
                }
                """));
        OpenAiCompatibleLlmProviderHandler handler = new OpenAiCompatibleLlmProviderHandler(
                new DefaultLlmCredentialResolver(),
                new OpenAiCompatibleChatClient(new ObjectMapper(), httpExecutor));

        LlmInvocationResult result = handler.generate(invocationRequest("sk-real-key"));

        assertFalse(result.mock());
        assertEquals("真实模型回答", result.answer());
        verify(httpExecutor).execute(any(LlmHttpRequest.class));
    }

    private static LlmInvocationRequest invocationRequest(String apiKey) {
        return new LlmInvocationRequest(
                "trace-test",
                "capability.chat.generate",
                "agent.chat",
                "bailian",
                "qwen-plus",
                "https://dashscope.aliyuncs.com/compatible-mode/v1",
                apiKey,
                30_000,
                null,
                "你好",
                Map.of()
        );
    }

    private static ModelDefinition model(String providerCode, String providerType) {
        return new ModelDefinition(
                providerCode,
                providerCode,
                providerType,
                "qwen-plus",
                "Qwen Plus",
                "CHAT",
                "https://dashscope.aliyuncs.com/compatible-mode/v1",
                "sk-test",
                30_000,
                1,
                CommonStatus.ENABLED,
                1,
                null
        );
    }
}
