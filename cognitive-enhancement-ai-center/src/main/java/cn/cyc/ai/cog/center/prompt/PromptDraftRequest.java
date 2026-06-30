package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

/**
 * 创建 Prompt 草稿版本请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record PromptDraftRequest(String promptCode, 
        String version,
        String promptName,
        String scenarioCode,
        String templateContent,
        SchemaDefinition variableSchema,
        SchemaDefinition outputSchema
) {
}
