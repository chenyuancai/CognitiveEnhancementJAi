package cn.cyc.ai.cog.runtime.harness.dto;

/**
 * Harness 执行响应。
 *
 * @param harnessId Harness 执行唯一标识
 * @param status    执行状态
 * @param message   状态说明
 * @author cyc
 */
public record HarnessRunResponse(
        String harnessId,
        String status,
        String message
) {
}
