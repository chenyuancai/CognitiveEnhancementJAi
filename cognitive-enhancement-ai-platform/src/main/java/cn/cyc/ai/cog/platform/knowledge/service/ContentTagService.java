package cn.cyc.ai.cog.platform.knowledge.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentTag;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentTagPageQuery;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentTagSaveRequest;
import cn.cyc.ai.cog.platform.knowledge.repository.ContentTagRepository;
import org.springframework.stereotype.Service;

@Service
public class ContentTagService {

    private final ContentTagRepository contentTagRepository;

    public ContentTagService(ContentTagRepository contentTagRepository) {
        this.contentTagRepository = contentTagRepository;
    }

    public PageResult<ContentTag> page(ContentTagPageQuery query) {
        return contentTagRepository.page(query);
    }

    public ContentTag create(ContentTagSaveRequest request) {
        return contentTagRepository.create(request);
    }

    public ContentTag update(Long id, ContentTagSaveRequest request) {
        return contentTagRepository.update(id, request);
    }

    public void delete(Long id) {
        contentTagRepository.delete(id);
    }
}
