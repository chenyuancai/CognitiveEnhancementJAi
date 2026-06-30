package cn.cyc.ai.cog.core.metadata.prompt;

/**
 * 租户维度 Prompt 发布指针，用于灰度选版。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record PromptReleasePointer(
        String tenantCode,
        String promptCode,
        String baselineVersion,
        String candidateVersion,
        PromptGrayRule grayRule
) {
}
