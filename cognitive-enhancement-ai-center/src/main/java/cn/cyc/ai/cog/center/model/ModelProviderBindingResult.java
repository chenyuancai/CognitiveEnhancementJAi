package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

/**
 * 模型与提供商绑定返回对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ModelProviderBindingResult(
        String providerCode,
        String providerName,
        String providerType,
        String endpoint,
        boolean apiKeyConfigured,
        String apiKeyMask,
        int routePriority,
        CommonStatus status
) {
}
