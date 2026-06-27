package cn.cyc.ai.cog.runtime.knowledge.dto;

/**
 * 创建场景知识绑定请求。
 *
 * @param scenarioCode  场景编码
 * @param knowledgeCode 知识库编码
 * @param priority      优先级
 * @param enabled       是否启用
 * @author cyc
 */
public record CreateScenarioBindingRequest(
        String scenarioCode,
        String knowledgeCode,
        int priority,
        boolean enabled
) {
}
