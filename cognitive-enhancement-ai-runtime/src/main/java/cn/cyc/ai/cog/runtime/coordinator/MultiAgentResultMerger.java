package cn.cyc.ai.cog.runtime.coordinator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 多 Agent 结果合并器：按策略汇总主 Agent 与子 Agent 业务输出。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class MultiAgentResultMerger {

    /**
     * 合并主 Agent 与子 Agent 输出到统一 businessOutput。
     *
     * @param primaryOutput    主 Agent 输出
     * @param delegateResults  子 Agent 结果
     * @param strategy         执行策略
     * @return 合并后的 businessOutput，无需合并时返回 null
     */
    public Map<String, Object> merge(Map<String, Object> primaryOutput,
                                     List<DelegateAgentResult> delegateResults,
                                     ExecutionStrategy strategy) {
        if (delegateResults == null || delegateResults.isEmpty()) {
            return null;
        }
        if (strategy == ExecutionStrategy.COST_FIRST) {
            return null;
        }
        List<String> mergedAnswers = new ArrayList<>();
        appendAnswer(mergedAnswers, extractAnswer(primaryOutput));
        for (DelegateAgentResult delegateResult : delegateResults) {
            appendAnswer(mergedAnswers, extractAnswer(delegateResult.output()));
        }
        if (mergedAnswers.isEmpty()) {
            return null;
        }
        Map<String, Object> merged = new LinkedHashMap<>();
        merged.put("answer", String.join("\n\n---\n\n", mergedAnswers));
        merged.put("mergedFromAgents", delegateResults.stream().map(DelegateAgentResult::agentCode).toList());
        merged.put("mergeStrategy", strategy.name());
        return merged;
    }

    /**
     * 执行extract回答。
     *
     * @param output 输出
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    private String extractAnswer(Map<String, Object> output) {
        if (output == null) {
            return null;
        }
        Object businessOutput = output.get("businessOutput");
        if (businessOutput instanceof Map<?, ?> businessMap) {
            Object answer = businessMap.get("answer");
            if (answer != null) {
                return String.valueOf(answer);
            }
        }
        Object llmResult = output.get("llmResult");
        if (llmResult instanceof Map<?, ?> llmMap) {
            Object answer = llmMap.get("answer");
            if (answer != null) {
                return String.valueOf(answer);
            }
        }
        return null;
    }

    /**
     * 执行append回答。
     *
     * @param answers answers
     * @param answer 回答
     */
    private void appendAnswer(List<String> answers, String answer) {
        if (answer != null && !answer.isBlank()) {
            answers.add(answer.trim());
        }
    }
}
