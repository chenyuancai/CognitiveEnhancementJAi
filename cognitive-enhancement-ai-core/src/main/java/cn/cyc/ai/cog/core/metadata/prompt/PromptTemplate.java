package cn.cyc.ai.cog.core.metadata.prompt;

import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

import java.time.Instant;
import java.util.Objects;

/**
 * Prompt 模板定义对象。
 */
public record PromptTemplate(
        String promptCode,
        String promptName,
        String scenarioCode,
        String version,
        String templateContent,
        SchemaDefinition variableSchema,
        SchemaDefinition outputSchema,
        CommonStatus status,
        Instant publishedAt,
        PromptLifecycleStatus lifecycleStatus
) implements MetadataDefinition {

    public PromptTemplate {
        promptCode = Objects.requireNonNull(promptCode, "promptCode 不能为空");
        promptName = Objects.requireNonNull(promptName, "promptName 不能为空");
        scenarioCode = Objects.requireNonNull(scenarioCode, "scenarioCode 不能为空");
        version = Objects.requireNonNull(version, "version 不能为空");
        templateContent = Objects.requireNonNull(templateContent, "templateContent 不能为空");
        variableSchema = Objects.requireNonNull(variableSchema, "variableSchema 不能为空");
        outputSchema = Objects.requireNonNull(outputSchema, "outputSchema 不能为空");
        status = Objects.requireNonNull(status, "status 不能为空");
        if (lifecycleStatus == null) {
            lifecycleStatus = publishedAt != null ? PromptLifecycleStatus.PUBLISHED : PromptLifecycleStatus.DRAFT;
        }
    }

    /**
     * 兼容旧构造：未显式指定生命周期时，按 publishedAt 推断。
     */
    public PromptTemplate(String promptCode,
                          String promptName,
                          String scenarioCode,
                          String version,
                          String templateContent,
                          SchemaDefinition variableSchema,
                          SchemaDefinition outputSchema,
                          CommonStatus status,
                          Instant publishedAt) {
        this(promptCode, promptName, scenarioCode, version, templateContent, variableSchema, outputSchema,
                status, publishedAt, null);
    }

    @Override
    public String code() {
        return promptCode;
    }

    @Override
    public String name() {
        return promptName;
    }

    /**
     * 是否可被运行时直接解析（已发布且启用）。
     *
     * @return 是否运行时可见
     */
    public boolean runtimeVisible() {
        return status == CommonStatus.ENABLED && lifecycleStatus == PromptLifecycleStatus.PUBLISHED;
    }
}
