package cn.cyc.ai.cog.platform.knowledge.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentTag;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentTagPageQuery;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentTagSaveRequest;
import cn.cyc.ai.cog.platform.knowledge.repository.ContentTagRepository;
import org.springframework.stereotype.Service;

/**
 * 内容标签服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class ContentTagService {

    /** 内容标签仓储。 */
    private final ContentTagRepository contentTagRepository;

    /**
     * 创建内容标签服务。
     *
     * @param contentTagRepository 内容标签仓储
     */
    public ContentTagService(ContentTagRepository contentTagRepository) {
        this.contentTagRepository = contentTagRepository;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    public PageResult<ContentTag> page(ContentTagPageQuery query) {
        return contentTagRepository.page(query);
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    public ContentTag create(ContentTagSaveRequest request) {
        return contentTagRepository.create(request);
    }

    /**
     * 更新Item。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 更新结果
     */
    public ContentTag update(Long id, ContentTagSaveRequest request) {
        return contentTagRepository.update(id, request);
    }

    /**
     * 删除Item。
     *
     * @param id 主键 ID
     */
    public void delete(Long id) {
        contentTagRepository.delete(id);
    }
}
