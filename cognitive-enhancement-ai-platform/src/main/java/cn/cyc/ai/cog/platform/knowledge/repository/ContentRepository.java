package cn.cyc.ai.cog.platform.knowledge.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.domain.Content;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentTag;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentAuditRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentPageQuery;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentSaveRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentTagBindRequest;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentVersion;

import java.util.List;

/**
 * 内容仓储
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface ContentRepository {

    PageResult<Content> page(ContentPageQuery query);

    Content findById(Long id);

    List<ContentTag> findTagsByContentId(Long contentId);

    List<ContentTag> bindTags(Long contentId, ContentTagBindRequest request);

    Content create(ContentSaveRequest request);

    /**
     * 导入任务落库：指定租户，状态 DRAFT。
     */
    Content createImportDraft(Long tenantId, ContentSaveRequest request);

    Content update(Long id, ContentSaveRequest request);

    Content audit(Long id, ContentAuditRequest request);

    Content offline(Long id);

    List<ContentVersion> listVersions(Long contentId);

    Content rollbackToVersion(Long contentId, int versionNo);

    long countByTenant(Long tenantId);

    /**
     * 按状态统计内容数量。
     *
     * @param tenantId 租户 ID，可为 null
     * @param status   内容状态
     * @return 内容数量
     */
    long countByStatus(Long tenantId, String status);

    /**
     * 统计指定状态且在更新时间范围内的内容数量。
     *
     * @param tenantId 租户 ID，可为 null
     * @param status   内容状态
     * @param start    更新时间下限
     * @param end      更新时间上限
     * @return 内容数量
     */
    long countByStatusAndUpdateTimeBetween(Long tenantId, String status,
                                           java.time.LocalDateTime start, java.time.LocalDateTime end);
}
