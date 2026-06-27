package cn.cyc.ai.cog.center.model.catalog;

import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

import java.util.List;
import java.util.Objects;

/**
 * 模型主数据（含多提供商绑定）。
 */
public record ModelMasterDefinition(
        String modelCode,
        String modelName,
        String modelType,
        int timeoutMs,
        int retryTimes,
        CommonStatus status,
        String fallbackModelCode,
        List<ModelBindingDefinition> providerBindings
) implements MetadataDefinition {

    public ModelMasterDefinition {
        modelCode = Objects.requireNonNull(modelCode, "modelCode 不能为空");
        modelName = Objects.requireNonNull(modelName, "modelName 不能为空");
        modelType = Objects.requireNonNull(modelType, "modelType 不能为空");
        status = Objects.requireNonNull(status, "status 不能为空");
        providerBindings = providerBindings == null ? List.of() : List.copyOf(providerBindings);
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("timeoutMs 必须大于 0");
        }
        if (retryTimes < 0) {
            throw new IllegalArgumentException("retryTimes 不能小于 0");
        }
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
