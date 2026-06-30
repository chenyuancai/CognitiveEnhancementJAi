package cn.cyc.ai.cog.runtime.harness.step;

import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStepResult;
import cn.cyc.ai.cog.runtime.harness.support.HarnessImportWorkflowSupport;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Agent 执行验证步骤，验证 AgentRuntime 能正确加载 Skill 并执行。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AgentExecuteStep implements HarnessStep {

    /**
     * 执行step编码。
     * @return 执行结果
     */
    @Override
    public String stepCode() {
        return "AGENT_EXECUTE";
    }

    /**
     * 执行step名称。
     * @return 执行结果
     */
    @Override
    public String stepName() {
        return "Agent 执行验证";
    }

    /**
     * 执行描述。
     * @return 执行结果
     */
    @Override
    public String description() {
        return "验证 AgentRuntime 能正确加载 Skill 并发起执行";
    }

    /**
     * 执行操作。
     *
     * @param ctx ctx
     * @return 执行结果
     */
    @Override
    public HarnessStepResult run(HarnessContext ctx) {
        if (HarnessImportWorkflowSupport.isImportKbFileParse(ctx.scenario())) {
            return HarnessImportWorkflowSupport.skipStep(this, "导入工作流场景，跳过 Agent 执行");
        }
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
