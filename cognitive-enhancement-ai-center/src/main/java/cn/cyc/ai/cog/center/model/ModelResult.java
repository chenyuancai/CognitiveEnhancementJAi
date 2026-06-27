package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

import java.util.List;

/**
 * 模型管理返回对象。
 *
 * <p>{@code providers} 为全部绑定；{@code providerCode} 等字段为首选路由镜像，兼容旧前端。
 */
public record ModelResult(
        String providerCode,
        String providerName,
        String modelCode,
        String modelName,
        String modelType,
        String endpoint,
        boolean apiKeyConfigured,
        String apiKeyMask,
        int timeoutMs,
        int retryTimes,
        CommonStatus status,
        int routePriority,
        String fallbackModelCode,
        List<ModelProviderBindingResult> providers
) {
}
