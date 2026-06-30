package cn.cyc.ai.cog.center.model.provider;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

/**
 * 模型提供商写入请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ModelProviderUpsertRequest(
        String providerCode,
        String providerName,
        String providerType,
        String defaultEndpoint,
        String apiKey,
        String description,
        CommonStatus status
) {
}
