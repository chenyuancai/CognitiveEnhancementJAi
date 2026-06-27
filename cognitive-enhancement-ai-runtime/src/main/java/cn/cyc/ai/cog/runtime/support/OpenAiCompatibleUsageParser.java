package cn.cyc.ai.cog.runtime.support;

import cn.cyc.ai.cog.runtime.api.LlmTokenUsage;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * OpenAI 兼容协议响应中的 usage 解析器。
 *
 * @author cyc
 */
public final class OpenAiCompatibleUsageParser {

    private OpenAiCompatibleUsageParser() {
    }

    /**
     * 从响应根节点解析 token 用量。
     *
     * @param rootNode 响应根节点
     * @return token 用量，缺失时返回 {@link LlmTokenUsage#EMPTY}
     */
    public static LlmTokenUsage parseUsage(JsonNode rootNode) {
        if (rootNode == null) {
            return LlmTokenUsage.EMPTY;
        }
        JsonNode usageNode = rootNode.path("usage");
        if (usageNode.isMissingNode() || usageNode.isNull()) {
            return LlmTokenUsage.EMPTY;
        }
        int inputTokenCount = usageNode.path("prompt_tokens").asInt(0);
        int outputTokenCount = usageNode.path("completion_tokens").asInt(0);
        int totalTokenCount = usageNode.path("total_tokens").asInt(inputTokenCount + outputTokenCount);
        return new LlmTokenUsage(inputTokenCount, outputTokenCount, totalTokenCount);
    }
}
