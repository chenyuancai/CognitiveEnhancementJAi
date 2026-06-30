package cn.cyc.ai.cog.app.tutoring.govern;

import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringBlueprint;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringGovernanceResult;
import cn.cyc.ai.cog.app.tutoring.strategy.AppTutoringStrategyDecision;

/**
 * 辅导回答输出治理 SPI，用于约束过度代做与引用幻觉。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface AppTutoringOutputGovernor {

    /**
     * 对模型回答执行输出治理。
     *
     * @param answer    原始回答文本
     * @param blueprint 教学蓝图
     * @param decision  策略决策结果
     * @return 治理结果
     */
    AppTutoringGovernanceResult govern(String answer,
                                       AppTutoringBlueprint blueprint,
                                       AppTutoringStrategyDecision decision);
}
