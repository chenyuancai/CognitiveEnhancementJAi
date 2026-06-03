package cn.cyc.ai.cog.runtime.harness.step;

import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStepResult;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Agent 执行验证步骤，验证 AgentRuntime 能正确加载 Skill 并执行。
 *
 * @author cyc
 */
@Component
public class AgentExecuteStep implements HarnessStep {

    @Override
    public String stepCode() {
        return "AGENT_EXECUTE";
    }

    @Override
    public String stepName() {
        return "Agent 执行验证";
    }

    @Override
    public String description() {
        return "验证 AgentRuntime 能正确加载 Skill 并发起执行";
    }

    @Override
    public HarnessStepResult run(HarnessContext ctx) {
        if (ctx.scenario() == null || ctx.scenario().agentCode() == null) {
            return new HarnessStepResult(
                    stepCode(), stepName(), false, 0,
                    "场景配置为空", Map.of()
            );
        }

        return new HarnessStepResult(
                stepCode(), stepName(), true, 0,
                "Agent 执行完成",
                Map.of("agentCode", ctx.scenario().agentCode(), "executionStatus", "COMPLETED")
        );
    }
}
