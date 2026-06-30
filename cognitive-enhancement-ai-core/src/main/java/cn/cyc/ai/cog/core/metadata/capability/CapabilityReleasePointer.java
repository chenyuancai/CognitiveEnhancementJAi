package cn.cyc.ai.cog.core.metadata.capability;

import cn.cyc.ai.cog.core.metadata.prompt.PromptGrayRule;

/**
 * 租户维度 Capability 发布指针，用于灰度选版。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record CapabilityReleasePointer(
        String tenantCode,
        String capabilityCode,
        String baselineVersion,
        String candidateVersion,
        PromptGrayRule grayRule
) {
}
