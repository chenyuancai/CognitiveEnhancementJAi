package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.runtime.api.LlmConversationRequest;
import cn.cyc.ai.cog.runtime.api.LlmConversationResult;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;

/**
 * LLM 调用网关预留接口。
 *
 * @author cyc
 */
public interface LlmGateway {

    /**
     * 发起一次模型生成调用。
     */
    LlmInvocationResult generate(ExecutionContext context, ModelDefinition model, Object promptInput);

    /**
     * 多轮对话 + tools（ReAct）。
     */
    LlmConversationResult chat(ExecutionContext context, ModelDefinition model, LlmConversationRequest request);
}
