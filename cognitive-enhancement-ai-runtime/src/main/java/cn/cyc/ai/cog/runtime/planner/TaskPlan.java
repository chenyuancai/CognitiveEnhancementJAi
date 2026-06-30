package cn.cyc.ai.cog.runtime.planner;

import java.util.List;

/**
 * 任务规划结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record TaskPlan(String goal, List<TaskPlanStep> steps) {
}
