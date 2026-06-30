package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

import java.util.List;

/**
 * 模型管理写入请求。
 * <p>优先使用 {@code providerBindings} 维护多对多关系；API Key 默认继承提供商，绑定层可覆盖。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ModelUpsertRequest(
        String providerCode,
        String providerName,
        String modelCode,
        String modelName,
        String modelType,
        String endpoint,
        String apiKey,
        int timeoutMs,
        int retryTimes,
        CommonStatus status,
        int routePriority,
        String fallbackModelCode,
        List<ModelProviderBindingRequest> providerBindings
) {
}
