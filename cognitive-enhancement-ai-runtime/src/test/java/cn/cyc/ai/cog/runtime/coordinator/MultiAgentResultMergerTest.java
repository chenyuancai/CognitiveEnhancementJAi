package cn.cyc.ai.cog.runtime.coordinator;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 多 Agent 结果合并器测试。
 */
class MultiAgentResultMergerTest {

    private final MultiAgentResultMerger merger = new MultiAgentResultMerger();

    @Test
    void shouldMergeAnswersForQualityFirstStrategy() {
        Map<String, Object> primaryOutput = Map.of(
                "businessOutput", Map.of("answer", "主回答"));
        List<DelegateAgentResult> delegates = List.of(
                new DelegateAgentResult("agent.research", "OK", Map.of(
                        "businessOutput", Map.of("answer", "子回答"))));

        Map<String, Object> merged = merger.merge(primaryOutput, delegates, ExecutionStrategy.QUALITY_FIRST);

        assertNotNull(merged);
        assertEquals("主回答\n\n---\n\n子回答", merged.get("answer"));
    }

    @Test
    void shouldSkipMergeForCostFirstStrategy() {
        Map<String, Object> primaryOutput = new LinkedHashMap<>(Map.of(
                "businessOutput", Map.of("answer", "主回答")));
        List<DelegateAgentResult> delegates = List.of(
                new DelegateAgentResult("agent.research", "OK", Map.of(
                        "businessOutput", Map.of("answer", "子回答"))));

        assertNull(merger.merge(primaryOutput, delegates, ExecutionStrategy.COST_FIRST));
    }
}
