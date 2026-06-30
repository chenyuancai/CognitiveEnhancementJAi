package cn.cyc.ai.cog.runtime.knowledge.dto;

/**
 * 创建场景知识绑定请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record CreateScenarioBindingRequest(
        String scenarioCode,
        String knowledgeCode,
        int priority,
        boolean enabled
) {
}
