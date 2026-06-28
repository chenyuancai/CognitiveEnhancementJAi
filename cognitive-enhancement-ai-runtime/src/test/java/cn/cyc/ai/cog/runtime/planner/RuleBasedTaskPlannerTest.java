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

/**
 * 规则型任务规划器测试。
 */
class RuleBasedTaskPlannerTest {

    @Test
    void shouldBuildPlanWhenPlanningEnabled() {
        RuleBasedTaskPlanner planner = new RuleBasedTaskPlanner();
        ExecutionContext context = new ExecutionContext(
                "trace-plan",
                new CapabilityExecuteRequest(
                        "cap.chat",
                        Map.of("question", "解释 Spring Boot"),
                        Map.of("planningEnabled", true)),
                sampleCapability(),
                null,
                null,
                List.of(),
                Map.of());

        TaskPlan plan = planner.plan(context).orElseThrow();
        assertEquals("解释 Spring Boot", plan.goal());
        assertEquals(3, plan.steps().size());
        assertEquals("ANALYZE", plan.steps().get(0).action());
        assertEquals("tool.search", plan.steps().get(1).toolCode());
    }

    @Test
    void shouldSkipPlanWhenPlanningDisabled() {
        RuleBasedTaskPlanner planner = new RuleBasedTaskPlanner();
        ExecutionContext context = new ExecutionContext(
                "trace-plan-off",
                new CapabilityExecuteRequest(
                        "cap.chat",
                        Map.of("question", "hello"),
                        Map.of()),
                sampleCapability(),
                null,
                null,
                List.of(),
                Map.of());

        assertTrue(planner.plan(context).isEmpty());
    }

    private CapabilityDefinition sampleCapability() {
        SchemaDefinition schema = new SchemaDefinition("object", "schema", true, Map.of(), null, List.of());
        return new CapabilityDefinition(
                "cap.chat",
                "Chat",
                "desc",
                schema,
                schema,
                Map.of(),
                ExecutionMode.SYNC,
                "agent.chat",
                RiskLevel.LOW,
                false,
                CommonStatus.ENABLED);
    }
}
