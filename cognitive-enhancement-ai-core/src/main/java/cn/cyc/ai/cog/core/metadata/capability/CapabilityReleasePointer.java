package cn.cyc.ai.cog.core.metadata.capability;

import cn.cyc.ai.cog.core.metadata.prompt.PromptGrayRule;

/**
 * 租户维度 Capability 发布指针，用于灰度选版。
 *
 * @param tenantCode        租户编码
 * @param capabilityCode    能力编码
 * @param baselineVersion   基线版本
 * @param candidateVersion  候选版本，可为 null 表示无灰度
 * @param grayRule          灰度规则
 * @author cyc
 */
public record CapabilityReleasePointer(
        String tenantCode,
        String capabilityCode,
        String baselineVersion,
        String candidateVersion,
        PromptGrayRule grayRule
) {
}
