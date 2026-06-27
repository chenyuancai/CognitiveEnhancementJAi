package cn.cyc.ai.cog.core.metadata.prompt;

/**
 * 租户维度 Prompt 发布指针，用于灰度选版。
 *
 * @param tenantCode       租户编码
 * @param promptCode       Prompt 编码
 * @param baselineVersion  基线版本
 * @param candidateVersion 候选版本，可为 null 表示无灰度
 * @param grayRule         灰度规则
 * @author cyc
 */
public record PromptReleasePointer(
        String tenantCode,
        String promptCode,
        String baselineVersion,
        String candidateVersion,
        PromptGrayRule grayRule
) {
}
