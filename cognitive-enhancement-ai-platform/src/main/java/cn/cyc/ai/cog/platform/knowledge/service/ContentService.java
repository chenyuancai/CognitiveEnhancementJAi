package cn.cyc.ai.cog.platform.knowledge.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.domain.Content;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentTag;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentVersion;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentAuditRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentPageQuery;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentRollbackRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentSaveRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentTagBindRequest;
import cn.cyc.ai.cog.platform.knowledge.repository.ContentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 内容服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class ContentService {

    /** 内容仓储。 */
    private final ContentRepository contentRepository;

    /**
     * 创建内容服务。
     *
     * @param contentRepository 内容仓储
     */
    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    public PageResult<Content> page(ContentPageQuery query) {
        return contentRepository.page(query);
    }

    /**
     * 执行detail。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    public Content detail(Long id) {
        return contentRepository.findById(id);
    }

    /**
     * 查询Tags列表。
     *
     * @param contentId 内容ID
     * @return 结果列表
     */
    public List<ContentTag> listTags(Long contentId) {
        return contentRepository.findTagsByContentId(contentId);
    }

    /**
     * 执行bindTags。
     *
     * @param contentId 内容ID
     * @param request 请求
     * @return 执行结果
     */
    public List<ContentTag> bindTags(Long contentId, ContentTagBindRequest request) {
        return contentRepository.bindTags(contentId, request);
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    public Content create(ContentSaveRequest request) {
        return contentRepository.create(request);
    }

    /**
     * 更新Item。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 更新结果
     */
    public Content update(Long id, ContentSaveRequest request) {
        return contentRepository.update(id, request);
    }

    /**
     * 执行audit。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 执行结果
     */
    public Content audit(Long id, ContentAuditRequest request) {
        return contentRepository.audit(id, request);
    }

    /**
     * 执行offline。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    public Content offline(Long id) {
        return contentRepository.offline(id);
    }

    /**
     * 查询Versions列表。
     *
     * @param id 主键 ID
     * @return 结果列表
     */
    public List<ContentVersion> listVersions(Long id) {
        return contentRepository.listVersions(id);
    }

    /**
     * 执行rollback。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 执行结果
     */
    public Content rollback(Long id, ContentRollbackRequest request) {
        return contentRepository.rollbackToVersion(id, request.getVersionNo());
    }

    /**
     * 执行数量人租户。
     *
     * @param tenantId 租户 ID
     * @return 执行结果
     */
    public long countByTenant(Long tenantId) {
        return contentRepository.countByTenant(tenantId);
    }
}
