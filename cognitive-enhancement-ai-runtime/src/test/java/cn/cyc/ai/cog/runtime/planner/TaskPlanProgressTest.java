package cn.cyc.ai.cog.runtime.planner;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 任务规划进度测试。
 */
class TaskPlanProgressTest {

    @Test
    void shouldMarkAllStepsDone() {
        TaskPlan plan = new TaskPlan("goal", List.of(
                new TaskPlanStep(1, "A", "step-a", "PLANNED"),
                new TaskPlanStep(2, "B", "step-b", "EXECUTING")));

        TaskPlan updated = TaskPlanProgress.markAllDone(plan);

        assertEquals("DONE", updated.steps().get(0).status());
        assertEquals("DONE", updated.steps().get(1).status());
    }
}
