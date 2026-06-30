package cn.cyc.ai.cog.center.model.provider;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

/**
 * 模型提供商返回对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ModelProviderResult(
        String providerCode,
        String providerName,
        String providerType,
        String defaultEndpoint,
        boolean apiKeyConfigured,
        String apiKeyMask,
        String description,
        CommonStatus status
) {
}
