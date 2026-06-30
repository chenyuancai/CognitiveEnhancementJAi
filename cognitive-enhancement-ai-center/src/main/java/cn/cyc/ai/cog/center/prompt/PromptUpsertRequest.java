package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

import java.time.Instant;

/**
 * Prompt 模板写入请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record PromptUpsertRequest(
        String promptCode,
        String promptName,
        String scenarioCode,
        String version,
        String templateContent,
        SchemaDefinition variableSchema,
        SchemaDefinition outputSchema,
        CommonStatus status,
        Instant publishedAt
) {
}
