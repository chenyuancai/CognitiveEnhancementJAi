package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

/**
 * Model DTO 定义。
 */
public final class ModelDtos {

    private ModelDtos() {
    }

    public record CreateRequest(
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
            String fallbackModelCode
    ) {
    }

    public record UpdateRequest(
            String providerCode,
            String providerName,
            String modelName,
            String modelType,
            String endpoint,
            String apiKey,
            int timeoutMs,
            int retryTimes,
            CommonStatus status,
            int routePriority,
            String fallbackModelCode
    ) {
    }

    public record Result(
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
            String fallbackModelCode
    ) {
    }
}
