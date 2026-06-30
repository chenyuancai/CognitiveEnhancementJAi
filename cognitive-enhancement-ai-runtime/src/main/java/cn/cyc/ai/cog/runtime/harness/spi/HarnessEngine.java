package cn.cyc.ai.cog.runtime.harness.spi;

import cn.cyc.ai.cog.runtime.harness.domain.HarnessReport;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Harness 执行引擎，负责串行调度所有验证步骤。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface HarnessEngine {

    /**
     * 执行 Harness 验证。
     *
     * <p>执行策略：
     * <ul>
     *   <li>步骤按传入顺序串行执行</li>
     *   <li>任一步骤失败后，后续步骤标记为 SKIPPED</li>
     *   <li>每个步骤独立捕获异常，不中断报告生成</li>
     * </ul>
     *
     * @param steps   验证步骤列表
     * @param context 执行上下文
     * @return Harness 完整报告
     */
    default HarnessReport run(List<HarnessStep> steps, HarnessContext context) {
        return run(steps, context, null);
    }

    /**
     * 执行 Harness 验证，支持每步回调。
     *
     * <p>执行策略：
     * <ul>
     *   <li>步骤按传入顺序串行执行</li>
     *   <li>任一步骤失败后，后续步骤标记为 SKIPPED</li>
     *   <li>每个步骤独立捕获异常，不中断报告生成</li>
     *   <li>每完成一个步骤，调用 stepCallback（如果非 null）</li>
     * </ul>
     *
     * @param steps        验证步骤列表
     * @param context      执行上下文
     * @param stepCallback 每步完成后的回调，可为 null
     * @return Harness 完整报告
     */
    default HarnessReport run(List<HarnessStep> steps, HarnessContext context,
                              Consumer<HarnessReport.HarnessStepReport> stepCallback) {
        return run(steps, context, stepCallback, HarnessCancellation.none());
    }

    /**
     * 执行 Harness 验证，支持每步回调与取消令牌。
     *
     * @param steps        验证步骤列表
     * @param context      执行上下文
     * @param stepCallback 每步完成后的回调，可为 null
     * @param cancellation 取消令牌，可为 {@link HarnessCancellation#none()}
     * @return Harness 完整报告
     */
    HarnessReport run(List<HarnessStep> steps, HarnessContext context,
                      Consumer<HarnessReport.HarnessStepReport> stepCallback,
                      HarnessCancellation cancellation);
}
