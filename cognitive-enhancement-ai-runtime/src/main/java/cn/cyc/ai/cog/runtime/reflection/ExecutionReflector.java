package cn.cyc.ai.cog.runtime.reflection;

import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.support.RuntimeContextParameters;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpanType;
import cn.cyc.ai.cog.runtime.trace.span.TraceSpanRecorder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 执行自反思器：对低质量 LLM 回答进行一次修正重试。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class ExecutionReflector {

    /** properties。 */
    private final ReflectionProperties properties;
    /** 链路SpanRecorder。 */
    private final TraceSpanRecorder traceSpanRecorder;

    /**
     * 创建ExecutionReflector。
     *
     * @param properties properties
     * @param traceSpanRecorder 链路SpanRecorder
     */
    public ExecutionReflector(ReflectionProperties properties, TraceSpanRecorder traceSpanRecorder) {
        this.properties = properties;
        this.traceSpanRecorder = traceSpanRecorder;
    }

    /**
     * 若回答质量不足则触发反思重试。
     *
     * @param context            运行时上下文
     * @param originalPromptInput 原始提示词输入
     * @param initialResult      首次 LLM 结果
     * @param llmInvoker         LLM 重试调用器
     * @return 反思结果
     */
    public ReflectionOutcome reflectIfNeeded(ExecutionContext context,
                                             Object originalPromptInput,
                                             LlmInvocationResult initialResult,
                                             Function<Object, LlmInvocationResult> llmInvoker) {
        if (!properties.isEnabled() || !RuntimeContextParameters.flag(context, "reflectionEnabled")) {
            return ReflectionOutcome.unchanged(initialResult);
        }
        int maxRetries = resolveMaxRetries(context);
        if (maxRetries <= 0 || !needsReflection(initialResult)) {
            return ReflectionOutcome.unchanged(initialResult);
        }

        TraceSpanRecorder.SpanScope reflectionSpan = traceSpanRecorder.open(
                context.traceId(),
                TraceSpanType.REFLECTION,
                "quality-refine",
                Map.of("initialAnswerLength", answerLength(initialResult)));
        try {
            Object reflectionPrompt = buildReflectionPrompt(originalPromptInput, initialResult);
            LlmInvocationResult refined = llmInvoker.apply(reflectionPrompt);
            traceSpanRecorder.succeed(reflectionSpan, Map.of("retryCount", 1));
            return new ReflectionOutcome(refined, true, 1, "已基于首次回答进行质量反思重试");
        } catch (RuntimeException ex) {
            traceSpanRecorder.fail(reflectionSpan, ex, Map.of("failureReason", ex.getMessage()));
            return ReflectionOutcome.unchanged(initialResult);
        }
    }

    /**
     * 执行resolveMaxRetries。
     *
     * @param context 上下文
     * @return 执行结果
     */
    private int resolveMaxRetries(ExecutionContext context) {
        return RuntimeContextParameters.integer(context, "reflectionMaxRetries")
                .filter(value -> value >= 0)
                .orElse(properties.getMaxRetries());
    }

    /**
     * 执行needsReflection。
     *
     * @param result 结果
     * @return 执行结果
     */
    private boolean needsReflection(LlmInvocationResult result) {
        String answer = result.answer();
        if (!StringUtils.hasText(answer)) {
            return true;
        }
        if (answer.trim().length() < properties.getMinAnswerLength()) {
            return true;
        }
        String normalized = answer.toLowerCase();
        return properties.getFailureKeywords().stream()
                .filter(StringUtils::hasText)
                .map(String::toLowerCase)
                .anyMatch(normalized::contains);
    }

    /**
     * 构建Reflection提示词。
     *
     * @param originalPromptInput original提示词输入
     * @param initialResult initial结果
     * @return 构建结果
     */
    private Object buildReflectionPrompt(Object originalPromptInput, LlmInvocationResult initialResult) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("instruction", """
                你正在做回答质量反思。请基于原始输入重新给出更完整、可执行的答案。
                不要重复“无法回答”类措辞，若信息不足请给出可落地的下一步建议。""");
        payload.put("originalPromptInput", originalPromptInput);
        payload.put("previousAnswer", initialResult.answer());
        payload.put("reflectionReason", "initial answer quality insufficient");
        return payload;
    }

    /**
     * 执行回答Length。
     *
     * @param result 结果
     * @return 执行结果
     */
    private int answerLength(LlmInvocationResult result) {
        return result.answer() == null ? 0 : result.answer().length();
    }
}
