package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.domain.ExecutionRecord;

/**
 * 执行记录器。
 *
 * @author cyc
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
}
