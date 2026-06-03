package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.harness.PolicyHarness;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import org.springframework.stereotype.Component;

/**
 * PolicyHarness 默认实现。
 *
 * @author cyc
 */
@Component
public class DefaultPolicyHarness implements PolicyHarness {

    /**
     * 执行策略检查，直接通过。
     *
     * @param capability 能力定义
     * @param context    执行上下文
     * @return 允许执行的策略决策
     */
    @Override
    public PolicyDecision evaluate(CapabilityDefinition capability, ExecutionContext context) {
        return new PolicyDecision(
                true,
                "策略检查通过",
                capability.riskLevel().name(),
                capability.needHumanConfirm()
        );
    }
}
