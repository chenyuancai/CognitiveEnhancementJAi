package cn.cyc.ai.cog.core.metadata.model;

import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

import java.util.Objects;

/**
 * 模型定义对象。
 *
 * @author cyc
 */
public record ModelDefinition(
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
) implements MetadataDefinition {

    public ModelDefinition {
        providerCode = Objects.requireNonNull(providerCode, "providerCode 不能为空");
        providerName = Objects.requireNonNull(providerName, "providerName 不能为空");
        modelCode = Objects.requireNonNull(modelCode, "modelCode 不能为空");
        modelName = Objects.requireNonNull(modelName, "modelName 不能为空");
        modelType = Objects.requireNonNull(modelType, "modelType 不能为空");
        endpoint = Objects.requireNonNull(endpoint, "endpoint 不能为空");
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("timeoutMs 必须大于 0");
        }
        if (retryTimes < 0) {
            throw new IllegalArgumentException("retryTimes 不能小于 0");
        }
        if (routePriority < 0) {
            throw new IllegalArgumentException("routePriority 不能小于 0");
        }
        status = Objects.requireNonNull(status, "status 不能为空");
    }

    @Override
    public String code() {
        return modelCode;
    }

    @Override
    public String name() {
        return modelName;
    }
}
