package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.runtime.api.LlmInvocationRequest;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;

/**
 * LLM Provider 处理器接口。
 *
 * @author cyc
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
}
