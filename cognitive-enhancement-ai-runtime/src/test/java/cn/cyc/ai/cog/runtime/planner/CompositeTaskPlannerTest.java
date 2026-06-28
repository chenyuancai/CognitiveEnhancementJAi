package cn.cyc.ai.cog.runtime.planner;

import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 组合任务规划器测试。
 */
class CompositeTaskPlannerTest {

    @Test
    void shouldRouteToRulePlannerByDefault() {
        PlanningProperties properties = new PlanningProperties();
        RuleBasedTaskPlanner rulePlanner = new RuleBasedTaskPlanner();
        LlmTaskPlanner llmPlanner = mock(LlmTaskPlanner.class);
        CompositeTaskPlanner planner = new CompositeTaskPlanner(properties, rulePlanner, llmPlanner);
        ExecutionContext context = context(Map.of("planningEnabled", true));

        TaskPlan plan = planner.plan(context).orElseThrow();
        assertEquals(3, plan.steps().size());
    }

    @Test
    void shouldRouteToLlmPlannerWhenModeIsLlm() {
        PlanningProperties properties = new PlanningProperties();
        RuleBasedTaskPlanner rulePlanner = new RuleBasedTaskPlanner();
        LlmTaskPlanner llmPlanner = mock(LlmTaskPlanner.class);
        when(llmPlanner.plan(org.mockito.ArgumentMatchers.any()))
                .thenReturn(java.util.Optional.of(new TaskPlan("goal", List.of(
                        new TaskPlanStep(1, "RESEARCH", "检索资料", "PLANNED")))));
        CompositeTaskPlanner planner = new CompositeTaskPlanner(properties, rulePlanner, llmPlanner);
        ExecutionContext context = context(Map.of("planningEnabled", true, "planningMode", "LLM"));

        TaskPlan plan = planner.plan(context).orElseThrow();
        assertEquals("RESEARCH", plan.steps().get(0).action());
    }

    @Test
    void shouldSkipWhenPlannerDisabled() {
        PlanningProperties properties = new PlanningProperties();
        properties.setEnabled(false);
        CompositeTaskPlanner planner = new CompositeTaskPlanner(
                properties,
                new RuleBasedTaskPlanner(),
                mock(LlmTaskPlanner.class));

        assertTrue(planner.plan(context(Map.of("planningEnabled", true))).isEmpty());
    }

    private ExecutionContext context(Map<String, Object> parameters) {
        SchemaDefinition schema = new SchemaDefinition("object", "schema", true, Map.of(), null, List.of());
        CapabilityDefinition capability = new CapabilityDefinition(
                "cap.chat", "Chat", "desc", schema, schema, Map.of(),
                ExecutionMode.SYNC, "agent.chat", RiskLevel.LOW, false, CommonStatus.ENABLED);
        return new ExecutionContext(
                "trace-composite",
                new CapabilityExecuteRequest("cap.chat", Map.of("question", "hello"), parameters),
                capability,
                null,
                null,
                List.of(),
                Map.of());
    }
}
