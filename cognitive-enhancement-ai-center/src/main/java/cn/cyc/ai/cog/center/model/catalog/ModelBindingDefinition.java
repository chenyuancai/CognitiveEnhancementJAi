package cn.cyc.ai.cog.center.model.catalog;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

import java.util.Objects;

/**
 * 模型与提供商的多对多绑定。
 */
public record ModelBindingDefinition(
        String modelCode,
        String providerCode,
        String endpoint,
        String apiKey,
        int routePriority,
        CommonStatus status
) {

    public ModelBindingDefinition {
        modelCode = Objects.requireNonNull(modelCode, "modelCode 不能为空");
        providerCode = Objects.requireNonNull(providerCode, "providerCode 不能为空");
        status = Objects.requireNonNull(status, "status 不能为空");
        if (routePriority < 0) {
            throw new IllegalArgumentException("routePriority 不能小于 0");
        }
    }
}
