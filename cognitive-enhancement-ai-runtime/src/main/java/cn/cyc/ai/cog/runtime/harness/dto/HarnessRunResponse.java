package cn.cyc.ai.cog.runtime.harness.dto;

/**
 * Harness 执行响应。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record HarnessRunResponse(
        String harnessId,
        String status,
        String message
) {
}
