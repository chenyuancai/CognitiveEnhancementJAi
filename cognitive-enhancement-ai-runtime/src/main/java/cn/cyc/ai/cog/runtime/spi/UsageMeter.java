package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.runtime.api.ExecutionResult;
import cn.cyc.ai.cog.runtime.domain.ExecutionContext;
import cn.cyc.ai.cog.runtime.domain.UsageRecord;

/**
 * 用量记录器。
 *
 * @author cyc
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
