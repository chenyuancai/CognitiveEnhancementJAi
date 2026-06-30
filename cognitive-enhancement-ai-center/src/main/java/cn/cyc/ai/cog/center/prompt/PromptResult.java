package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.core.metadata.prompt.PromptLifecycleStatus;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

import java.time.Instant;

/**
 * Prompt 模板返回对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record PromptResult(
        String promptCode,
        String promptName,
        String scenarioCode,
        String version,
        String templateContent,
        SchemaDefinition variableSchema,
        SchemaDefinition outputSchema,
        CommonStatus status,
        PromptLifecycleStatus lifecycleStatus,
        Instant publishedAt
) {
}
