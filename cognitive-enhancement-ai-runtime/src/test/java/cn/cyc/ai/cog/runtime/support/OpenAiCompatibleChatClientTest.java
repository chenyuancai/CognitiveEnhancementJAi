package cn.cyc.ai.cog.runtime.support;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.api.ChatMessage;
import cn.cyc.ai.cog.runtime.api.LlmConversationRequest;
import cn.cyc.ai.cog.runtime.api.LlmHttpRequest;
import cn.cyc.ai.cog.runtime.api.LlmHttpResponse;
import cn.cyc.ai.cog.runtime.api.LlmInvocationRequest;
import cn.cyc.ai.cog.runtime.spi.LlmHttpExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenAiCompatibleChatClientTest {

    @Test
    void complete_shouldUseModelTimeoutFromRequest() {
        LlmHttpExecutor httpExecutor = mock(LlmHttpExecutor.class);
        when(httpExecutor.execute(any(LlmHttpRequest.class))).thenAnswer(invocation -> {
            LlmHttpRequest request = invocation.getArgument(0);
            assertTrue(request.timeout().toMillis() <= 100L);
            throw new BusinessException("CONFLICT", "LLM HTTP 调用失败: request timed out");
        });
        OpenAiCompatibleChatClient client = new OpenAiCompatibleChatClient(new ObjectMapper(), httpExecutor);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> client.complete(invocationRequest(100), "sk-test", "/chat/completions"));
        assertTrue(exception.getMessage().contains("timed out"));
    }

    @Test
    void completeConversation_shouldRejectEmptyAssistantMessageWithoutToolCalls() {
        LlmHttpExecutor httpExecutor = mock(LlmHttpExecutor.class);
        when(httpExecutor.execute(any(LlmHttpRequest.class))).thenReturn(new LlmHttpResponse(200, """
                {
                  "choices": [
                    {
                      "finish_reason": "stop",
                      "message": {
                        "content": null
                      }
                    }
                  ],
                  "usage": {
                    "prompt_tokens": 7,
                    "completion_tokens": 0,
                    "total_tokens": 7
                  }
                }
                """));
        OpenAiCompatibleChatClient client = new OpenAiCompatibleChatClient(new ObjectMapper(), httpExecutor);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> client.completeConversation(
                        "qwen-plus",
                        "https://dashscope.aliyuncs.com/compatible-mode/v1",
                        "sk-test",
                        conversationRequest(),
                        "/chat/completions"));

        assertTrue(exception.getMessage().contains("缺少 message.content 或 tool_calls"));
    }

    @Test
    void completeConversation_shouldRejectMalformedToolCallWithoutName() {
        LlmHttpExecutor httpExecutor = mock(LlmHttpExecutor.class);
        when(httpExecutor.execute(any(LlmHttpRequest.class))).thenReturn(new LlmHttpResponse(200, """
                {
                  "choices": [
                    {
                      "finish_reason": "tool_calls",
                      "message": {
                        "content": null,
                        "tool_calls": [
                          {
                            "id": "call-1",
                            "type": "function",
                            "function": {
                              "arguments": "{\\"question\\":\\"退款政策\\"}"
                            }
                          }
                        ]
                      }
                    }
                  ],
                  "usage": {
                    "prompt_tokens": 10,
                    "completion_tokens": 2,
                    "total_tokens": 12
                  }
                }
                """));
        OpenAiCompatibleChatClient client = new OpenAiCompatibleChatClient(new ObjectMapper(), httpExecutor);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> client.completeConversation(
                        "qwen-plus",
                        "https://dashscope.aliyuncs.com/compatible-mode/v1",
                        "sk-test",
                        conversationRequest(),
                        "/chat/completions"));

        assertTrue(exception.getMessage().contains("tool_calls.function.name"));
    }

    private static LlmConversationRequest conversationRequest() {
        return new LlmConversationRequest(
                List.of(ChatMessage.user("hello")),
                List.of(),
                Map.of(),
                30_000
        );
    }

    private static LlmInvocationRequest invocationRequest(int timeoutMs) {
        return new LlmInvocationRequest(
                "trace-timeout",
                "capability.chat.generate",
                "agent.chat",
                "bailian",
                "qwen-plus",
                "https://dashscope.aliyuncs.com/compatible-mode/v1",
                "sk-test-key",
                timeoutMs,
                null,
                "hello",
                Map.of()
        );
    }
}
