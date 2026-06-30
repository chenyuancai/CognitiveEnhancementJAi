package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.runtime.api.LlmConversationRequest;
import cn.cyc.ai.cog.runtime.api.LlmConversationResult;
import cn.cyc.ai.cog.runtime.api.LlmInvocationRequest;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.spi.LlmCredentialResolver;
import cn.cyc.ai.cog.runtime.spi.LlmProviderHandler;
import cn.cyc.ai.cog.runtime.support.OpenAiCompatibleChatClient;
import cn.cyc.ai.cog.runtime.support.TokenCountEstimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * OpenAI-compatible 协议 Provider 处理器（OpenAI / 百炼 / 其他兼容端点）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class OpenAiCompatibleLlmProviderHandler implements LlmProviderHandler {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(OpenAiCompatibleLlmProviderHandler.class);
    /** CHATCOMPLETIONS路径。 */
    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";
    /** SUPPORTED提供者TYPES。 */
    private static final Set<String> SUPPORTED_PROVIDER_TYPES = Set.of("OPENAI_COMPATIBLE", "DASHSCOPE");
    /** LEGACY提供者CODES。 */
    private static final Set<String> LEGACY_PROVIDER_CODES = Set.of("openai", "bailian", "dashscope");

    /** llmCredentialResolver。 */
    private final LlmCredentialResolver llmCredentialResolver;
    /** openAiCompatibleChat客户端。 */
    private final OpenAiCompatibleChatClient openAiCompatibleChatClient;

    /**
     * 创建OpenAiCompatibleLlm提供者处理器。
     */
    public OpenAiCompatibleLlmProviderHandler(LlmCredentialResolver llmCredentialResolver,
                                              OpenAiCompatibleChatClient openAiCompatibleChatClient) {
        this.llmCredentialResolver = llmCredentialResolver;
        this.openAiCompatibleChatClient = openAiCompatibleChatClient;
    }

    /**
     * 执行supports。
     *
     * @param model 模型
     * @return 执行结果
     */
    @Override
    public boolean supports(ModelDefinition model) {
        if (model == null) {
            return false;
        }
        String providerType = model.providerType() == null ? "" : model.providerType().trim().toUpperCase();
        if (SUPPORTED_PROVIDER_TYPES.contains(providerType)) {
            return true;
        }
        return LEGACY_PROVIDER_CODES.contains(model.providerCode());
    }

    /**
     * 执行generate。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Override
    public LlmInvocationResult generate(LlmInvocationRequest request) {
        String apiKey = llmCredentialResolver.resolve(request.apiKey());
        OpenAiCompatibleChatClient.ChatCompletionResult chatCompletion =
                openAiCompatibleChatClient.complete(request, apiKey, CHAT_COMPLETIONS_PATH);
        log.info("执行 OpenAI-compatible 模型调用完成, traceId={}, providerCode={}, modelCode={}, endpoint={}, latencyMs={}, totalTokens={}",
                request.traceId(), request.providerCode(), request.modelCode(), request.endpoint(),
                chatCompletion.latencyMs(), chatCompletion.tokenUsage().totalTokenCount());
        return new LlmInvocationResult(
                "LLM",
                request.providerCode(),
                request.modelCode(),
                request.promptCode(),
                request.promptInput(),
                chatCompletion.answer(),
                request.parameters(),
                chatCompletion.tokenUsage().inputTokenCount(),
                chatCompletion.tokenUsage().outputTokenCount(),
                chatCompletion.tokenUsage().totalTokenCount(),
                chatCompletion.latencyMs(),
                false
        );
    }

    /**
     * 执行chat。
     *
     * @param model 模型
     * @param request 请求
     * @return 执行结果
     */
    @Override
    public LlmConversationResult chat(ModelDefinition model, LlmConversationRequest request) {
        String apiKey = llmCredentialResolver.resolve(model.apiKey());
        LlmConversationResult result = openAiCompatibleChatClient.completeConversation(
                model.modelCode(),
                model.endpoint(),
                apiKey,
                request,
                CHAT_COMPLETIONS_PATH);
        log.info("执行 OpenAI-compatible ReAct Chat 调用完成, providerCode={}, modelCode={}, latencyMs={}, totalTokens={}",
                model.providerCode(), model.modelCode(), result.latencyMs(), result.totalTokenCount());
        return result;
    }
}
