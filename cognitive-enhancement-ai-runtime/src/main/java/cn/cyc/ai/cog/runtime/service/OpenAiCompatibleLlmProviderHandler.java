package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.runtime.api.LlmInvocationRequest;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.spi.LlmCredentialResolver;
import cn.cyc.ai.cog.runtime.spi.LlmProviderHandler;
import cn.cyc.ai.cog.runtime.support.OpenAiCompatibleChatClient;
import cn.cyc.ai.cog.runtime.support.TokenCountEstimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * OpenAI-compatible 协议 Provider 处理器（OpenAI / 百炼 / 其他兼容端点）。
 */
@Component
public class OpenAiCompatibleLlmProviderHandler implements LlmProviderHandler {

    private static final Logger log = LoggerFactory.getLogger(OpenAiCompatibleLlmProviderHandler.class);
    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    private final LlmCredentialResolver llmCredentialResolver;
    private final OpenAiCompatibleChatClient openAiCompatibleChatClient;

    public OpenAiCompatibleLlmProviderHandler(LlmCredentialResolver llmCredentialResolver,
                                              OpenAiCompatibleChatClient openAiCompatibleChatClient) {
        this.llmCredentialResolver = llmCredentialResolver;
        this.openAiCompatibleChatClient = openAiCompatibleChatClient;
    }

    @Override
    public boolean supports(String providerCode) {
        return true;
    }

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
}
