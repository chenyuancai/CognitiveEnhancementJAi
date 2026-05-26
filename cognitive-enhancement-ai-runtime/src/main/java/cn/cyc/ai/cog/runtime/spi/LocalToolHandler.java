package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.runtime.api.ToolInvocationRequest;
import cn.cyc.ai.cog.runtime.domain.ExecutionContext;

/**
 * 本地 Tool 处理器。
 *
 * @author cyc
 */
public interface LocalToolHandler {

    /**
     * 返回处理器绑定的实现引用编码。
     *
     * @return 实现引用编码
     */
    String implRef();

    /**
     * 执行本地 Tool。
     *
     * @param context 运行时上下文
     * @param request Tool 调用请求
     * @return 执行结果
     */
    Object invoke(ExecutionContext context, ToolInvocationRequest request);
}
