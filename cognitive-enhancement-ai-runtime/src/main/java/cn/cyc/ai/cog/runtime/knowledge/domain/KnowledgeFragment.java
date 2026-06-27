package cn.cyc.ai.cog.runtime.knowledge.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;
import java.util.List;

/**
 * 知识片段。
 *
 * @param tenantCode    租户编码
 * @param fragmentId    片段 ID
 * @param knowledgeCode 知识库编码
 * @param title         标题
 * @param content       内容
 * @param tags          标签列表
 * @param status        状态
 * @param recordedAt    记录时间
 * @author cyc
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
