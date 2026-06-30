package cn.cyc.ai.cog.runtime.domain;

import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;

/**
 * AgentRuntime 输出。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record AgentRuntimeResult(
        ExecutionContext context,
        ExecutionResult result
) {
}
