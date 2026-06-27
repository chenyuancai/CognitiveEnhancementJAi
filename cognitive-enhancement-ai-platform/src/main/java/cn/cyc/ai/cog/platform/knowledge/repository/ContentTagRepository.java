package cn.cyc.ai.cog.platform.knowledge.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentTag;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentTagPageQuery;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentTagSaveRequest;

public interface ContentTagRepository {

    PageResult<ContentTag> page(ContentTagPageQuery query);

    ContentTag create(ContentTagSaveRequest request);

    ContentTag update(Long id, ContentTagSaveRequest request);

    void delete(Long id);
}
