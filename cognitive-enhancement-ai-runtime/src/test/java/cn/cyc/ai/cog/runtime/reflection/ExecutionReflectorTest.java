package cn.cyc.ai.cog.runtime.reflection;

import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.trace.span.TraceSpanRecorder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * 自反思器测试。
 */
class ExecutionReflectorTest {

    @Test
    void shouldRetryWhenAnswerTooShort() {
        ReflectionProperties properties = new ReflectionProperties();
        properties.setMinAnswerLength(10);
        ExecutionReflector reflector = new ExecutionReflector(properties, mock(TraceSpanRecorder.class));
        ExecutionContext context = context(Map.of("reflectionEnabled", true));
        LlmInvocationResult initial = llmResult("短答");
        LlmInvocationResult refined = llmResult("这是经过反思后的完整回答内容");

        ReflectionOutcome outcome = reflector.reflectIfNeeded(
                context,
                Map.of("question", "hello"),
                initial,
                ignored -> refined);

        assertTrue(outcome.applied());
        assertEquals(1, outcome.retryCount());
        assertEquals(refined.answer(), outcome.result().answer());
    }

    @Test
    void shouldSkipWhenReflectionDisabled() {
        ExecutionReflector reflector = new ExecutionReflector(new ReflectionProperties(), mock(TraceSpanRecorder.class));
        LlmInvocationResult initial = llmResult("不知道");

        ReflectionOutcome outcome = reflector.reflectIfNeeded(
                context(Map.of()),
                Map.of("question", "hello"),
                initial,
                ignored -> llmResult("retry"));

        assertFalse(outcome.applied());
        assertEquals(initial, outcome.result());
    }

    private ExecutionContext context(Map<String, Object> parameters) {
        return new ExecutionContext(
                "trace-reflect",
                new CapabilityExecuteRequest("cap.chat", Map.of("question", "hello"), parameters),
                null,
                null,
                null,
                List.of(),
                Map.of());
    }

    private LlmInvocationResult llmResult(String answer) {
        return new LlmInvocationResult(
                "LLM",
                "openai",
                "gpt-4o-mini",
                "prompt.chat",
                "prompt",
                answer,
                Map.of(),
                1,
                1,
                2,
                10L,
                true);
    }
}
