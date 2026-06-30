package cn.cyc.ai.cog.runtime.observation.spi;

import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;

/**
 * 用量记录器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface UsageMeter {

    /**
     * 记录一次运行时用量摘要。
     *
     * @param context 运行时上下文
     * @param result  执行结果
     * @return 用量记录
     */
    UsageRecord record(ExecutionContext context, ExecutionResult result);
}
