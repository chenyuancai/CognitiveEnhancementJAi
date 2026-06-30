package cn.cyc.ai.cog.runtime.harness.spi;

import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;

/**
 * Harness 验证步骤接口，每个步骤独立完成一项验证。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface HarnessStep {

    /**
     * 获取步骤编码。
     *
     * @return 步骤编码，如 "BEAN_VALIDATION"
     */
    String stepCode();

    /**
     * 获取步骤名称。
     *
     * @return 步骤名称，如 "组件装配检查"
     */
    String stepName();

    /**
     * 获取步骤描述。
     *
     * @return 步骤描述
     */
    String description();

    /**
     * 执行验证步骤。
     *
     * @param ctx 执行上下文
     * @return 步骤执行结果
     */
    HarnessStepResult run(HarnessContext ctx);
}
