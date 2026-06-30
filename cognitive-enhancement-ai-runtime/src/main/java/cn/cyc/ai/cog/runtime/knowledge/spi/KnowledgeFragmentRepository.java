package cn.cyc.ai.cog.runtime.knowledge.spi;

import cn.cyc.ai.cog.runtime.knowledge.domain.KnowledgeFragment;

import java.util.List;
import java.util.Optional;

/**
 * 知识片段仓储接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface KnowledgeFragmentRepository {

    /**
     * 保存知识片段。
     *
     * @param fragment 知识片段
     */
    void save(KnowledgeFragment fragment);

    /**
     * 按片段 ID 查询当前租户知识片段。
     *
     * @param fragmentId 片段 ID
     * @return 知识片段
     */
    Optional<KnowledgeFragment> findByFragmentId(String fragmentId);

    /**
     * 按知识库编码查询当前租户知识片段列表。
     *
     * @param knowledgeCode 知识库编码
     * @return 知识片段列表
     */
    List<KnowledgeFragment> findByKnowledgeCode(String knowledgeCode);

    /**
     * 查询当前租户全部知识片段。
     *
     * @return 知识片段列表
     */
    List<KnowledgeFragment> listAll();
}
