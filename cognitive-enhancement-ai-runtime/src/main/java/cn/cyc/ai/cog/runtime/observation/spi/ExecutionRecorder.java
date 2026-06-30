package cn.cyc.ai.cog.runtime.observation.spi;

import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.observation.domain.ExecutionRecord;

/**
 * 执行记录器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface ExecutionRecorder {

    /**
     * 记录一次能力执行摘要。
     *
     * @param context 运行时上下文
     * @param result  执行结果
     * @return 执行记录
     */
    ExecutionRecord record(ExecutionContext context, ExecutionResult result);

    /**
     * 记录一次能力执行失败摘要。
     *
     * @param context       运行时上下文
     * @param failureReason 执行失败原因
     * @return 执行记录
     */
    ExecutionRecord recordFailure(ExecutionContext context, String failureReason);
}
