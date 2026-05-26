package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.runtime.api.LlmInvocationRequest;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.spi.LlmProviderHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * OpenAI provider 的一期 mock 处理器。
 *
 * @author cyc
 */
@Component
public class MockOpenAiLlmProviderHandler implements LlmProviderHandler {

    /**
     * 处理器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(MockOpenAiLlmProviderHandler.class);

    /**
     * OpenAI provider 编码。
     */
    private static final String PROVIDER_CODE = "openai";

    /**
     * 判断是否支持 OpenAI provider。
     *
     * @param providerCode 模型提供方编码
     * @return 是否支持
     */
    @Override
    public boolean supports(String providerCode) {
        return PROVIDER_CODE.equals(providerCode);
    }

    /**
     * 执行一次 OpenAI mock 调用。
     *
     * @param request LLM 调用请求
     * @return LLM 调用结果
     */
    @Override
    public LlmInvocationResult generate(LlmInvocationRequest request) {
        log.info("执行 OpenAI mock 调用, traceId={}, capabilityCode={}, agentCode={}, modelCode={}",
                request.traceId(), request.capabilityCode(), request.agentCode(), request.modelCode());
        return new LlmInvocationResult(
                "LLM",
                request.providerCode(),
                request.modelCode(),
                request.promptCode(),
                request.promptInput(),
                "这是一期 mock LLM 输出，后续可接入真实 provider。",
                request.parameters(),
                true
        );
    }
}
