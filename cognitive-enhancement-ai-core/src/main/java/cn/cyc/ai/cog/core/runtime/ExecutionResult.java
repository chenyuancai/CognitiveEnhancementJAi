package cn.cyc.ai.cog.core.runtime;

import java.util.List;
import java.util.Map;

/**
 * 一期统一执行结果。
 *
 * @param status            执行状态
 * @param message           状态说明
 * @param allowedSkillCodes 参与执行的技能编码列表
 * @param output            输出载荷
 * @author cyc
 */
public record ExecutionResult(
        String status,
        String message,
        List<String> allowedSkillCodes,
        Map<String, Object> output
) {

    /**
     * 构造统一执行结果并收敛集合型字段。
     */
    public ExecutionResult {
        allowedSkillCodes = List.copyOf(allowedSkillCodes == null ? List.of() : allowedSkillCodes);
        output = Map.copyOf(output == null ? Map.of() : output);
    }
}
