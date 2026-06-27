package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

/**
 * 模型与提供商绑定写入请求。
 */
public record ModelProviderBindingRequest(
        String providerCode,
        String endpoint,
        String apiKey,
        Integer routePriority,
        CommonStatus status
) {
}
