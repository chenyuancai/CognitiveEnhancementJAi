package cn.cyc.ai.cog.runtime.tool.adapter.spi;

import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.api.ToolInvocationRequest;

/**
 * Tool Adapter 调用上下文。
 *
 * @param executionContext 执行上下文
 * @param tool             Tool 定义
 * @param request          Tool 调用请求
 * @author cyc
 */
public record ToolAdapterContext(
        ExecutionContext executionContext,
        ToolDefinition tool,
        ToolInvocationRequest request
) {
}
