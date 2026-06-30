package cn.cyc.ai.cog.center.model.provider;

import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

import java.util.Objects;

/**
 * 模型提供商定义。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ModelProviderDefinition(
        String providerCode,
        String providerName,
        String providerType,
        String defaultEndpoint,
        String apiKey,
        String description,
        CommonStatus status
) implements MetadataDefinition {

    public ModelProviderDefinition {
        providerCode = Objects.requireNonNull(providerCode, "providerCode 不能为空");
        providerName = Objects.requireNonNull(providerName, "providerName 不能为空");
        providerType = Objects.requireNonNull(providerType, "providerType 不能为空");
        status = Objects.requireNonNull(status, "status 不能为空");
    }

    /**
     * 执行编码。
     * @return 执行结果
     */
    @Override
    public String code() {
        return providerCode;
    }

    /**
     * 执行名称。
     * @return 执行结果
     */
    @Override
    public String name() {
        return providerName;
    }
}
