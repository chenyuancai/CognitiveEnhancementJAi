package cn.cyc.ai.cog.runtime.planner;

import java.util.List;

/**
 * 任务规划步骤进度更新工具。
 *
 * @author cyc
 */
public final class TaskPlanProgress {

    private TaskPlanProgress() {
    }

    /**
     * 更新指定步骤状态。
     *
     * @param plan   原规划
     * @param order  步骤序号
     * @param status 新状态
     * @return 更新后的规划
     */
    public static TaskPlan markStep(TaskPlan plan, int order, String status) {
        List<TaskPlanStep> steps = plan.steps().stream()
                .map(step -> step.order() == order
                        ? new TaskPlanStep(step.order(), step.action(), step.description(), status)
                        : step)
                .toList();
        return new TaskPlan(plan.goal(), steps);
    }

    /**
     * 将全部步骤标记为 DONE。
     *
     * @param plan 原规划
     * @return 更新后的规划
     */
    public static TaskPlan markAllDone(TaskPlan plan) {
        List<TaskPlanStep> steps = plan.steps().stream()
                .map(step -> new TaskPlanStep(step.order(), step.action(), step.description(), "DONE"))
                .toList();
        return new TaskPlan(plan.goal(), steps);
    }
}
