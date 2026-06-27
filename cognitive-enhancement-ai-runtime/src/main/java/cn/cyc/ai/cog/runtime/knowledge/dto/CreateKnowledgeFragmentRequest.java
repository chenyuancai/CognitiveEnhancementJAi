package cn.cyc.ai.cog.runtime.knowledge.dto;

import cn.cyc.ai.cog.runtime.knowledge.domain.KnowledgeFragmentStatus;

import java.util.List;

/**
 * 创建知识片段请求。
 *
 * @param knowledgeCode 知识库编码
 * @param title         标题
 * @param content       内容
 * @param tags          标签列表
 * @param status        状态
 * @author cyc
 */
public record CreateKnowledgeFragmentRequest(
        String knowledgeCode,
        String title,
        String content,
        List<String> tags,
        KnowledgeFragmentStatus status
) {
}
