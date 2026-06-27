package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

/**
 * 创建 Prompt 草稿版本请求。
 *
 * @param version         新版本号，为空时自动递增 patch
 * @param promptName      名称，为空则继承最新版本
 * @param scenarioCode    场景编码
 * @param templateContent 模板内容
 * @param variableSchema  变量 Schema
 * @param outputSchema    输出 Schema
 * @author cyc
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
