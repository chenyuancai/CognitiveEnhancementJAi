package cn.cyc.ai.cog.runtime.support;

import cn.cyc.ai.cog.runtime.api.LlmTokenUsage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * OpenAI 兼容 usage 解析器测试。
 *
 * @author cyc
 */
class OpenAiCompatibleUsageParserTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void parseUsage_shouldReadPromptCompletionAndTotalTokens() throws Exception {
        var rootNode = objectMapper.readTree("""
                {
                  "usage": {
                    "prompt_tokens": 11,
                    "completion_tokens": 7,
                    "total_tokens": 18
                  }
                }
                """);

        LlmTokenUsage usage = OpenAiCompatibleUsageParser.parseUsage(rootNode);

        assertEquals(11, usage.inputTokenCount());
        assertEquals(7, usage.outputTokenCount());
        assertEquals(18, usage.totalTokenCount());
    }

    @Test
    void parseUsage_shouldReturnEmptyWhenUsageMissing() throws Exception {
        var rootNode = objectMapper.readTree("""
                {
                  "choices": []
                }
                """);

        LlmTokenUsage usage = OpenAiCompatibleUsageParser.parseUsage(rootNode);

        assertEquals(LlmTokenUsage.EMPTY, usage);
    }
}
