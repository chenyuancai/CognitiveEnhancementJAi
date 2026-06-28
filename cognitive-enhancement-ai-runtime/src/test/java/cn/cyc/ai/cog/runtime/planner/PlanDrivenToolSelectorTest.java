package cn.cyc.ai.cog.runtime.planner;

import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 计划驱动 Tool 选择器测试。
 */
class PlanDrivenToolSelectorTest {

    private final PlanDrivenToolSelector selector = new PlanDrivenToolSelector();

    @Test
    void shouldPreferExplicitPreferredToolCode() {
        ExecutionContext context = context(Map.of("preferredToolCode", "tool.echo", "planningEnabled", true));
        String selected = selector.selectTool(
                List.of("tool.search", "tool.echo"),
                Optional.empty(),
                context);
        assertEquals("tool.echo", selected);
    }

    @Test
    void shouldSelectToolFromTaskPlanStep() {
        ExecutionContext context = context(Map.of("planningEnabled", true));
        TaskPlan plan = new TaskPlan("goal", List.of(
                new TaskPlanStep(1, "ANALYZE", "分析", "PLANNED"),
                new TaskPlanStep(2, "TOOL", "调用 tool.echo", "PLANNED", "tool.echo")));
        String selected = selector.selectTool(
                List.of("tool.search", "tool.echo"),
                Optional.of(plan),
                context);
        assertEquals("tool.echo", selected);
    }

    @Test
    void shouldFallbackToFirstToolWhenNoPlanMatch() {
        ExecutionContext context = context(Map.of());
        String selected = selector.selectTool(
                List.of("tool.search", "tool.echo"),
                Optional.empty(),
                context);
        assertEquals("tool.search", selected);
    }

    private ExecutionContext context(Map<String, Object> parameters) {
        return new ExecutionContext(
                "trace-plan-tool",
                new CapabilityExecuteRequest("capability.qa.answer", Map.of("question", "hello"), parameters),
                null,
                null,
                null,
                List.of(),
                Map.of());
    }
}
