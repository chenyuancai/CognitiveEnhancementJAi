package cn.cyc.ai.cog.runtime.knowledge.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;

/**
 * 场景知识绑定。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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
