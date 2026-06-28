package cn.cyc.ai.cog.runtime.budget;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.usage.service.RuntimeUsageProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 任务预算控制器测试。
 */
class TaskBudgetControllerTest {

    private final TaskBudgetProperties properties = new TaskBudgetProperties();
    private final RuntimeUsageProperties usageProperties = new RuntimeUsageProperties();
    private final TaskBudgetController controller = new TaskBudgetController(properties, usageProperties);

    @AfterEach
    void tearDown() {
        controller.clear();
    }

    @Test
    void shouldChargeToolAndLlmWithinBudget() {
        ExecutionContext context = context(Map.of("taskBudgetAmount", new BigDecimal("5.000000")));
        controller.start(context, sampleAgent(new BigDecimal("10.000000")));
        controller.chargeTool();
        controller.chargeLlm(new LlmInvocationResult(
                "LLM",
                "openai",
                "gpt-4o-mini",
                "prompt.chat",
                "hello",
                "answer",
                Map.of(),
                10,
                20,
                30,
                100L,
                true));
        assertEquals(new BigDecimal("3.999700"), controller.remaining().orElseThrow());
    }

    @Test
    void shouldRejectWhenBudgetExceeded() {
        ExecutionContext context = context(Map.of("taskBudgetAmount", new BigDecimal("0.500000")));
        controller.start(context, sampleAgent(new BigDecimal("10.000000")));
        assertThrows(BusinessException.class, controller::chargeTool);
    }

    private ExecutionContext context(Map<String, Object> parameters) {
        return new ExecutionContext(
                "trace-budget",
                new CapabilityExecuteRequest("cap.chat", Map.of("question", "hi"), parameters),
                null,
                null,
                null,
                List.of(),
                Map.of());
    }

    private AgentDefinition sampleAgent(BigDecimal maxCost) {
        return new AgentDefinition(
                "agent.chat",
                "Chat Agent",
                "role",
                "goal",
                "gpt-4o-mini",
                8,
                maxCost,
                30000,
                List.of("skill.chat"),
                Map.of(),
                CommonStatus.ENABLED);
    }
}
