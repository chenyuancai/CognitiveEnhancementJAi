package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.api.LlmConversationRequest;
import cn.cyc.ai.cog.runtime.api.LlmConversationResult;
import cn.cyc.ai.cog.runtime.api.LlmInvocationRequest;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;

/**
 * LLM Provider 处理器接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface LlmProviderHandler {

    /**
     * 判断是否支持指定模型路由。
     *
     * @param model 模型路由
     * @return 是否支持
     */
    boolean supports(ModelDefinition model);

    /**
     * 发起一次 provider 调用。
     *
     * @param request LLM 调用请求
     * @return LLM 调用结果
     */
    LlmInvocationResult generate(LlmInvocationRequest request);

    /**
     * 发起一次多轮对话 provider 调用，默认 provider 不支持 ReAct Chat。
     *
     * @param model   模型路由
     * @param request 多轮对话请求
     * @return 多轮对话结果
     */
    default LlmConversationResult chat(ModelDefinition model, LlmConversationRequest request) {
        throw new BusinessException("CONFLICT", "Provider 不支持 ReAct Chat: " + model.providerCode());
    }
}
