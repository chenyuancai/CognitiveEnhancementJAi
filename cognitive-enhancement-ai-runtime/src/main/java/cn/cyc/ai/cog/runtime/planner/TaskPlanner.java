package cn.cyc.ai.cog.runtime.planner;

import cn.cyc.ai.cog.core.runtime.ExecutionContext;

import java.util.Optional;

/**
 * 任务规划器 SPI。
 *
 * @author cyc
 */
public interface TaskPlanner {

    /**
     * 根据上下文生成任务规划。
     *
     * @param context 运行时上下文
     * @return 任务规划，未启用时为空
     */
    Optional<TaskPlan> plan(ExecutionContext context);
}
