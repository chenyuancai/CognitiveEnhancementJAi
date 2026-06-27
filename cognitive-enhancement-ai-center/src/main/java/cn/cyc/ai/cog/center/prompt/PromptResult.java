package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.core.metadata.prompt.PromptLifecycleStatus;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

import java.time.Instant;

/**
 * Prompt 模板返回对象。
 *
 * @param promptCode      Prompt 编码
 * @param promptName      Prompt 名称
 * @param scenarioCode    场景编码
 * @param version         版本号
 * @param templateContent 模板内容
 * @param variableSchema  变量结构定义
 * @param outputSchema    输出结构定义
 * @param status           启用状态
 * @param lifecycleStatus  生命周期状态
 * @param publishedAt      发布时间
 * @author cyc
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
