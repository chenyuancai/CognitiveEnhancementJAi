package cn.cyc.ai.cog.runtime.harness.spi;

import java.util.Map;

/**
 * Harness 步骤执行结果。
 *
 * @param stepCode   步骤编码
 * @param stepName   步骤名称
 * @param passed     是否通过
 * @param durationMs 执行耗时（毫秒）
 * @param message    结果说明（成功信息或错误详情）
 * @param details    扩展信息
 * @author cyc
 */
public record HarnessStepResult(
        String stepCode,
        String stepName,
        boolean passed,
        long durationMs,
        String message,
        Map<String, Object> details
) {
}
