package cn.cyc.ai.cog.platform.operations.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.Announcement;
import cn.cyc.ai.cog.platform.operations.dto.AnnouncementPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.AnnouncementSaveRequest;
import cn.cyc.ai.cog.platform.operations.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;

/**
 * Announcement服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AnnouncementService {

    /** announcement仓储。 */
    private final AnnouncementRepository announcementRepository;

    /**
     * 创建Announcement服务。
     *
     * @param announcementRepository announcement仓储
     */
    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    public PageResult<Announcement> page(AnnouncementPageQuery query) {
        return announcementRepository.page(query);
    }

    /**
     * 执行detail。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    public Announcement detail(Long id) {
        return announcementRepository.findById(id);
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    public Announcement create(AnnouncementSaveRequest request) {
        return announcementRepository.create(request);
    }

    /**
     * 更新Item。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 更新结果
     */
    public Announcement update(Long id, AnnouncementSaveRequest request) {
        return announcementRepository.update(id, request);
    }

    /**
     * 删除Item。
     *
     * @param id 主键 ID
     */
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

    /**
     * 查询Published列表。
     * @return 结果列表
     */
    public java.util.List<Announcement> listPublished() {
        return announcementRepository.listPublished();
    }
}
