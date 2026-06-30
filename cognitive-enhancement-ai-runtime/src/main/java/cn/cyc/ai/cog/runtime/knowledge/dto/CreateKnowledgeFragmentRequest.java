package cn.cyc.ai.cog.runtime.knowledge.dto;

import cn.cyc.ai.cog.runtime.knowledge.domain.KnowledgeFragmentStatus;

import java.util.List;

/**
 * 创建知识片段请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record CreateKnowledgeFragmentRequest(
        String knowledgeCode,
        String title,
        String content,
        List<String> tags,
        KnowledgeFragmentStatus status
) {
}
