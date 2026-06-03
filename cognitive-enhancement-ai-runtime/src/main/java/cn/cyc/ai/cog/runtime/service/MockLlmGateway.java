package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.runtime.api.LlmInvocationRequest;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.spi.LlmGateway;
import cn.cyc.ai.cog.runtime.spi.LlmProviderHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 一期默认 LLM Gateway mock 实现。
 *
 * @author cyc
 */
@Service
public class MockLlmGateway implements LlmGateway {

    /**
     * 网关日志。
     */
    private static final Logger log = LoggerFactory.getLogger(MockLlmGateway.class);

    /**
     * Provider 处理器列表。
     */
    private final List<LlmProviderHandler> llmProviderHandlers;

    /**
     * 构造默认 LLM Gateway。
     *
     * @param llmProviderHandlers Provider 处理器列表
     */
    public MockLlmGateway(List<LlmProviderHandler> llmProviderHandlers) {
        this.llmProviderHandlers = llmProviderHandlers;
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
        LlmProviderHandler handler = llmProviderHandlers.stream()
                .filter(candidate -> candidate.supports(model.providerCode()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("CONFLICT", "未找到可用的 LLM Provider 处理器: " + model.providerCode()));

        LlmInvocationRequest request = new LlmInvocationRequest(
                context.traceId(),
                context.capability().capabilityCode(),
                context.agent().agentCode(),
                model.providerCode(),
                model.modelCode(),
                model.endpoint(),
                model.credentialRef(),
                model.timeoutMs(),
                context.prompt() == null ? null : context.prompt().promptCode(),
                promptInput,
                context.request().parameters()
        );
        log.info("执行 LLM Gateway 路由, traceId={}, capabilityCode={}, agentCode={}, providerCode={}, modelCode={}",
                request.traceId(), request.capabilityCode(), request.agentCode(), request.providerCode(), request.modelCode());
        return handler.generate(request);
    }
}
