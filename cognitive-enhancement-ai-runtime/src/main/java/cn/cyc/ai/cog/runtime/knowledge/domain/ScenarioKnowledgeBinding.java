package cn.cyc.ai.cog.runtime.knowledge.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;

/**
 * 场景知识绑定。
 *
 * @param tenantCode    租户编码
 * @param bindingId     绑定 ID
 * @param scenarioCode  场景编码
 * @param knowledgeCode 知识库编码
 * @param priority      优先级，数值越小优先级越高
 * @param enabled       是否启用
 * @param recordedAt    记录时间
 * @author cyc
 */
public record ScenarioKnowledgeBinding(
        String tenantCode,
        String bindingId,
        String scenarioCode,
        String knowledgeCode,
        int priority,
        boolean enabled,
        Instant recordedAt
) {

    public ScenarioKnowledgeBinding {
        tenantCode = TenantContext.normalize(tenantCode);
    }
}
