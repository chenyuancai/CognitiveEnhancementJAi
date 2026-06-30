package cn.cyc.ai.cog.runtime.tool.adapter.spi;

import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.api.ToolInvocationRequest;

/**
 * Tool Adapter 调用上下文。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ToolAdapterContext(
        ExecutionContext executionContext,
        ToolDefinition tool,
        ToolInvocationRequest request
) {
}
