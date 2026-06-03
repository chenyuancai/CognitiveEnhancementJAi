package cn.cyc.ai.cog.core.harness;

import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;

/**
 * 策略治理器，负责执行前的策略检查与决策。
 *
 * <p>一期的职责：
 * <ul>
 *   <li>风险等级检查（RiskLevel）</li>
 *   <li>人工确认占位（needHumanConfirm）</li>
 *   <li>执行参数约束校验</li>
 * </ul>
 *
 * <p>二期可扩展：额度控制、访问权限、灰度策略、熔断决策。
 *
 * @author cyc
 */
public interface PolicyHarness {

    /**
     * 执行策略检查，返回是否允许执行。
     *
     * @param capability 能力定义
     * @param context    执行上下文
     * @return 策略决策结果
     */
    PolicyDecision evaluate(CapabilityDefinition capability, ExecutionContext context);

    /**
     * 策略决策结果。
     *
     * @param allowed          是否允许执行
     * @param reason           决策原因（拒绝时说明原因）
     * @param riskLevel        评估后的风险等级
     * @param needHumanConfirm 是否需要人工确认
     */
    record PolicyDecision(boolean allowed, String reason, String riskLevel, boolean needHumanConfirm) {
    }
}
