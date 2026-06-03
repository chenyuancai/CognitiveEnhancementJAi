package cn.cyc.ai.cog.runtime.domain;

import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;

/**
 * AgentRuntime 输出。
 *
 * @param context 执行后的上下文
 * @param result  执行结果
 * @author cyc
 */
public record AgentRuntimeResult(
        ExecutionContext context,
        ExecutionResult result
) {
}
