package cn.cyc.ai.cog.runtime.knowledge.service;

import cn.cyc.ai.cog.runtime.knowledge.domain.KnowledgeFragment;
import cn.cyc.ai.cog.runtime.knowledge.domain.KnowledgeFragmentStatus;
import cn.cyc.ai.cog.runtime.knowledge.spi.KnowledgeFragmentRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 知识片段服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class KnowledgeFragmentService {

    /**
     * 知识片段仓储。
     */
    private final KnowledgeFragmentRepository knowledgeFragmentRepository;

    /**
     * 构造知识片段服务。
     *
     * @param knowledgeFragmentRepository 知识片段仓储
     */
    public KnowledgeFragmentService(KnowledgeFragmentRepository knowledgeFragmentRepository) {
        this.knowledgeFragmentRepository = knowledgeFragmentRepository;
    }

    /**
     * 创建知识片段。
     *
     * @param knowledgeCode 知识库编码
     * @param title         标题
     * @param content       内容
     * @param tags          标签列表
     * @param status        状态
     * @return 新建知识片段
     */
    public KnowledgeFragment createFragment(String knowledgeCode,
                                            String title,
                                            String content,
                                            List<String> tags,
                                            KnowledgeFragmentStatus status) {
        KnowledgeFragment fragment = new KnowledgeFragment(
                TenantContext.currentTenantCode(),
                UUID.randomUUID().toString(),
                knowledgeCode,
                title,
                content,
                tags == null ? List.of() : tags,
                status == null ? KnowledgeFragmentStatus.ENABLED : status,
                Instant.now()
        );
        knowledgeFragmentRepository.save(fragment);
        return fragment;
    }

    /**
     * 查询知识片段列表。
     *
     * @param knowledgeCode 知识库编码筛选条件，可为空
     * @return 知识片段列表
     */
    public List<KnowledgeFragment> listFragments(String knowledgeCode) {
        if (StringUtils.hasText(knowledgeCode)) {
            return knowledgeFragmentRepository.findByKnowledgeCode(knowledgeCode);
        }
        return knowledgeFragmentRepository.listAll();
    }
}
