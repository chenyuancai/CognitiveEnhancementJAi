package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.center.common.SchemaDto;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

import java.time.Instant;

/**
 * Prompt DTO 定义。
 */
public final class PromptDtos {

    private PromptDtos() {
    }

    public record CreateRequest(
            String promptCode,
            String promptName,
            String scenarioCode,
            String version,
            String templateContent,
            SchemaDto variableSchema,
            SchemaDto outputSchema,
            CommonStatus status,
            Instant publishedAt
    ) {
    }

    public record UpdateRequest(
            String promptName,
            String scenarioCode,
            String version,
            String templateContent,
            SchemaDto variableSchema,
            SchemaDto outputSchema,
            CommonStatus status,
            Instant publishedAt
    ) {
    }

    public record Result(
            String promptCode,
            String promptName,
            String scenarioCode,
            String version,
            String templateContent,
            SchemaDto variableSchema,
            SchemaDto outputSchema,
            CommonStatus status,
            Instant publishedAt
    ) {
    }
}
