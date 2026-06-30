package cn.cyc.ai.cog.runtime.knowledge.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;
import java.util.List;

/**
 * 知识片段。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record KnowledgeFragment(
        String tenantCode,
        String fragmentId,
        String knowledgeCode,
        String title,
        String content,
        List<String> tags,
        KnowledgeFragmentStatus status,
        Instant recordedAt
) {

    public KnowledgeFragment {
        tenantCode = TenantContext.normalize(tenantCode);
    }
}
