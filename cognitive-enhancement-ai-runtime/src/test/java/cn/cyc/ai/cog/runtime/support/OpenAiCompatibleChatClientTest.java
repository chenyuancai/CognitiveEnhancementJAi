package cn.cyc.ai.cog.runtime.support;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.api.LlmHttpRequest;
import cn.cyc.ai.cog.runtime.api.LlmInvocationRequest;
import cn.cyc.ai.cog.runtime.spi.LlmHttpExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Duration;
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
