package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.runtime.api.LlmConversationRequest;
import cn.cyc.ai.cog.runtime.api.LlmConversationResult;
import cn.cyc.ai.cog.runtime.api.LlmInvocationRequest;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.spi.LlmGateway;
import cn.cyc.ai.cog.runtime.spi.LlmProviderHandler;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpanType;
import cn.cyc.ai.cog.runtime.trace.span.TraceSpanRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 默认 LLM Gateway 实现，按 Provider 路由模型调用。
 *
 * @author cyc
 */
@Service
public class DefaultLlmGateway implements LlmGateway {

    /**
     * 网关日志。
     */
    private static final Logger log = LoggerFactory.getLogger(DefaultLlmGateway.class);

    /**
     * Provider 处理器列表。
     */
    private final List<LlmProviderHandler> llmProviderHandlers;
    private final TraceSpanRecorder traceSpanRecorder;

    public DefaultLlmGateway(List<LlmProviderHandler> llmProviderHandlers,
                             TraceSpanRecorder traceSpanRecorder) {
        this.llmProviderHandlers = llmProviderHandlers;
        this.traceSpanRecorder = traceSpanRecorder;
    }

    /**
     * 按 provider 路由一次模型调用。
     *
     * @param context     运行时上下文
     * @param model       模型定义
     * @param promptInput 提示词输入
     * @return 模型输出结果
     */
    @Override
    public LlmInvocationResult generate(ExecutionContext context, ModelDefinition model, Object promptInput) {
        LlmProviderHandler handler = resolveHandler(model);

        LlmInvocationRequest request = new LlmInvocationRequest(
                context.traceId(),
                context.capability().capabilityCode(),
                context.agent().agentCode(),
                model.providerCode(),
                model.modelCode(),
                model.endpoint(),
                model.apiKey(),
                model.timeoutMs(),
                context.prompt() == null ? null : context.prompt().promptCode(),
                promptInput,
                context.request().parameters()
        );
        log.info("执行 LLM Gateway 路由, traceId={}, capabilityCode={}, agentCode={}, providerCode={}, modelCode={}",
                request.traceId(), request.capabilityCode(), request.agentCode(), request.providerCode(), request.modelCode());
        TraceSpanRecorder.SpanScope llmSpan = traceSpanRecorder.open(
                context.traceId(),
                TraceSpanType.LLM,
                model.modelCode(),
                Map.of("providerCode", model.providerCode(), "timeoutMs", model.timeoutMs()));
        try {
            LlmInvocationResult result = handler.generate(request);
            traceSpanRecorder.succeed(llmSpan, Map.of(
                    "mock", result.mock(),
                    "totalTokenCount", result.totalTokenCount()));
            return result;
        } catch (RuntimeException ex) {
            traceSpanRecorder.fail(llmSpan, ex, null);
            throw ex;
        }
    }

    @Override
    public LlmConversationResult chat(ExecutionContext context, ModelDefinition model, LlmConversationRequest request) {
        LlmProviderHandler handler = resolveHandler(model);
        TraceSpanRecorder.SpanScope llmSpan = traceSpanRecorder.open(
                context.traceId(),
                TraceSpanType.LLM,
                model.modelCode() + "-react",
                Map.of("providerCode", model.providerCode(), "messageCount", request.messages().size()));
        try {
            LlmConversationResult result = handler.chat(model, request);
            traceSpanRecorder.succeed(llmSpan, Map.of(
                    "toolCallCount", result.toolCalls().size(),
                    "totalTokenCount", result.totalTokenCount()));
            return result;
        } catch (RuntimeException ex) {
            traceSpanRecorder.fail(llmSpan, ex, null);
            throw ex;
        }
    }

    private LlmProviderHandler resolveHandler(ModelDefinition model) {
        return llmProviderHandlers.stream()
                .filter(candidate -> candidate.supports(model))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "CONFLICT",
                        "未找到可用的 LLM Provider 处理器: " + model.providerCode()));
    }
}
