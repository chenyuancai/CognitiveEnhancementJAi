package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.center.common.SchemaDto;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

import java.time.Instant;

/**
 * Prompt DTO 定义。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class PromptDtos {

    /**
     * 创建PromptDtos。
     */
    private PromptDtos() {
    }

    /**
     * 创建请求
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
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

    /**
     * 更新请求
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
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

    /**
     * Result 记录
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
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
