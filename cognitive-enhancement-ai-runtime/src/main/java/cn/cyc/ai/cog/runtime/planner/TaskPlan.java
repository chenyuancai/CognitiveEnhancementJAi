package cn.cyc.ai.cog.runtime.planner;

import java.util.List;

/**
 * 任务规划结果。
 *
 * @param goal  任务目标摘要
 * @param steps 规划步骤
 * @author cyc
 */
public record TaskPlan(String goal, List<TaskPlanStep> steps) {
}
