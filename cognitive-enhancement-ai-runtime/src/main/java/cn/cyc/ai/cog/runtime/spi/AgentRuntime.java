package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.runtime.domain.AgentRuntimeResult;
import cn.cyc.ai.cog.runtime.domain.ExecutionContext;

/**
 * Agent 运行时入口。
 *
 * @author cyc
 */
public interface AgentRuntime {

    /**
     * 执行 Agent 运行时逻辑。
     *
     * @param context 运行时上下文
     * @return Agent 运行结果
     */
    AgentRuntimeResult execute(ExecutionContext context);
}
