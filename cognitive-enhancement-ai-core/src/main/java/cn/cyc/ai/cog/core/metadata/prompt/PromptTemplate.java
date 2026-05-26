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
        Instant publishedAt
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
    }

    @Override
    public String code() {
        return promptCode;
    }

    @Override
    public String name() {
        return promptName;
    }
}
