package cn.cyc.ai.cog.core.harness;

import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;

/**
 * 运行时治理器，作为 Capability 执行的统一治理入口。
 *
 * <p>一期的职责：协调 TraceHarness → PolicyHarness → SkillLoader → OutputGovernance
 * 完成一次带治理的执行。
 *
 * <p>二期可扩展：执行前缓存、执行后审计、异常降级。
 *
 * @author cyc
 */
public interface RuntimeHarness {

    /**
     * 执行一次带完整治理的能力调用。
     *
     * <p>执行流程：
     * <ol>
     *   <li>TraceHarness.start() — 建立链路</li>
     *   <li>PolicyHarness.evaluate() — 策略检查</li>
     *   <li>CapabilityRuntime.execute() — 能力路由</li>
     *   <li>AgentRuntime.execute() — Agent 执行（内部使用 SkillLoader）</li>
     *   <li>OutputGovernance.govern() — 输出治理</li>
     *   <li>TraceHarness.finish() — 结束链路</li>
     * </ol>
     *
     * @param request 能力执行请求
     * @return 能力执行响应
     */
    CapabilityExecuteResponse execute(CapabilityExecuteRequest request);
}
