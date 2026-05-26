package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.domain.ExecutionContext;

/**
 * LLM 调用网关预留接口。
 *
 * @author cyc
 */
public interface LlmGateway {

    /**
     * 发起一次模型生成调用。
     *
     * @param context     运行时上下文
     * @param model       模型定义
     * @param promptInput 提示词输入
     * @return 模型输出
     */
    LlmInvocationResult generate(ExecutionContext context, ModelDefinition model, Object promptInput);
}
