package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.core.metadata.prompt.PromptGrayRule;

/**
 * 配置 Capability 灰度规则请求。
 *
 * @param capabilityCode 能力编码
 * @param grayRule       灰度规则
 * @author cyc
 */
public record CapabilityGrayRequest(String capabilityCode, PromptGrayRule grayRule) {
}
