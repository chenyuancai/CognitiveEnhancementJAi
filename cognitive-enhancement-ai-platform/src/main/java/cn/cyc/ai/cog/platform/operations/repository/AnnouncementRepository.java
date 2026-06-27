package cn.cyc.ai.cog.platform.operations.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.Announcement;
import cn.cyc.ai.cog.platform.operations.dto.AnnouncementPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.AnnouncementSaveRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface AnnouncementRepository {

    PageResult<Announcement> page(AnnouncementPageQuery query);

    Announcement findById(Long id);

    List<Announcement> listPublished();

    List<Announcement> findDueScheduled(LocalDateTime now);

    int publishDue(LocalDateTime now);

    Announcement create(AnnouncementSaveRequest request);

    Announcement update(Long id, AnnouncementSaveRequest request);

    void delete(Long id);
}
