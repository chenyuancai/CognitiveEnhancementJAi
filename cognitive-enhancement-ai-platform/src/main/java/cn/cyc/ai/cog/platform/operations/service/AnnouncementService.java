package cn.cyc.ai.cog.platform.operations.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.Announcement;
import cn.cyc.ai.cog.platform.operations.dto.AnnouncementPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.AnnouncementSaveRequest;
import cn.cyc.ai.cog.platform.operations.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    public PageResult<Announcement> page(AnnouncementPageQuery query) {
        return announcementRepository.page(query);
    }

    public Announcement detail(Long id) {
        return announcementRepository.findById(id);
    }

    public Announcement create(AnnouncementSaveRequest request) {
        return announcementRepository.create(request);
    }

    public Announcement update(Long id, AnnouncementSaveRequest request) {
        return announcementRepository.update(id, request);
    }

    public void delete(Long id) {
        announcementRepository.delete(id);
    }

    /**
     * 将到期的定时公告推进为已发布。
     *
     * @return 发布条数
     */
    public int publishDueScheduled() {
        return announcementRepository.publishDue(java.time.LocalDateTime.now());
    }

    public java.util.List<Announcement> listPublished() {
        return announcementRepository.listPublished();
    }
}
