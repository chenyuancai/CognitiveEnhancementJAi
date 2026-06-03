package cn.cyc.ai.cog.runtime.harness.step;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;
import cn.cyc.ai.cog.runtime.spi.CapabilityRuntime;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessScenario;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStepResult;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 能力路由验证步骤，验证 CapabilityRuntime 能正确路由到 Agent。
 *
 * @author cyc
 */
@Component
public class CapabilityRouteStep implements HarnessStep {

    private final CapabilityRuntime capabilityRuntime;

    public CapabilityRouteStep(CapabilityRuntime capabilityRuntime) {
        this.capabilityRuntime = capabilityRuntime;
    }

    @Override
    public String stepCode() {
        return "CAPABILITY_ROUTE";
    }

    @Override
    public String stepName() {
        return "能力路由验证";
    }

    @Override
    public String description() {
        return "验证 CapabilityRuntime 能正确路由到对应 Agent";
    }

    @Override
    public HarnessStepResult run(HarnessContext ctx) {
        HarnessScenario scenario = ctx.scenario();
        if (scenario == null || scenario.capabilityCode() == null) {
            return new HarnessStepResult(
                    stepCode(), stepName(), false, 0,
                    "场景配置为空或 capabilityCode 未指定", Map.of()
            );
        }

        long start = System.currentTimeMillis();
        try {
            CapabilityExecuteRequest request = new CapabilityExecuteRequest(
                    scenario.capabilityCode(), scenario.inputParams(), Map.of()
            );
            CapabilityExecuteResponse response = capabilityRuntime.execute(request);
            long durationMs = System.currentTimeMillis() - start;

            return new HarnessStepResult(
                    stepCode(), stepName(), true, durationMs,
                    "Capability 路由到 " + response.agent().agentCode() + " 成功",
                    Map.of("capabilityCode", scenario.capabilityCode(),
                            "agentCode", response.agent().agentCode(),
                            "executionStatus", "AGENT_INVOKED")
            );
        } catch (BusinessException ex) {
            return new HarnessStepResult(
                    stepCode(), stepName(), false, System.currentTimeMillis() - start,
                    "Capability 路由失败: " + ex.getMessage(),
                    Map.of("errorCode", ex.getCode())
            );
        } catch (Exception ex) {
            return new HarnessStepResult(
                    stepCode(), stepName(), false, System.currentTimeMillis() - start,
                    "Capability 路由异常: " + ex.getMessage(),
                    Map.of("exception", ex.getClass().getSimpleName())
            );
        }
    }
}
