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

@Service
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public PageResult<Content> page(ContentPageQuery query) {
        return contentRepository.page(query);
    }

    public Content detail(Long id) {
        return contentRepository.findById(id);
    }

    public List<ContentTag> listTags(Long contentId) {
        return contentRepository.findTagsByContentId(contentId);
    }

    public List<ContentTag> bindTags(Long contentId, ContentTagBindRequest request) {
        return contentRepository.bindTags(contentId, request);
    }

    public Content create(ContentSaveRequest request) {
        return contentRepository.create(request);
    }

    public Content update(Long id, ContentSaveRequest request) {
        return contentRepository.update(id, request);
    }

    public Content audit(Long id, ContentAuditRequest request) {
        return contentRepository.audit(id, request);
    }

    public Content offline(Long id) {
        return contentRepository.offline(id);
    }

    public List<ContentVersion> listVersions(Long id) {
        return contentRepository.listVersions(id);
    }

    public Content rollback(Long id, ContentRollbackRequest request) {
        return contentRepository.rollbackToVersion(id, request.getVersionNo());
    }

    public long countByTenant(Long tenantId) {
        return contentRepository.countByTenant(tenantId);
    }
}
