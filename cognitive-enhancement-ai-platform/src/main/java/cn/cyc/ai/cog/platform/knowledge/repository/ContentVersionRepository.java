package cn.cyc.ai.cog.platform.knowledge.repository;

import cn.cyc.ai.cog.platform.knowledge.domain.ContentVersion;

import java.util.List;
import java.util.Optional;

/**
 * 内容版本号仓储
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface ContentVersionRepository {

    void append(ContentVersion version);

    List<ContentVersion> listByContentId(Long contentId);

    Optional<ContentVersion> find(Long contentId, int versionNo);
}
