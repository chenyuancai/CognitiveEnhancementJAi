package cn.cyc.ai.cog.center.model;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

/**
 * Model DTO 定义。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class ModelDtos {

    /**
     * 创建ModelDtos。
     */
    private ModelDtos() {
    }

    /**
     * 创建请求
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
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

    /**
     * 更新请求
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
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

    /**
     * Result 记录
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
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
