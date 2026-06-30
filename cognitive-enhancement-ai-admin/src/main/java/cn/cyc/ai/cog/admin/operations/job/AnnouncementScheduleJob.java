package cn.cyc.ai.cog.admin.operations.job;

import cn.cyc.ai.cog.platform.operations.service.AnnouncementService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 公告定时发布任务：DRAFT 且 publish_at 到期 → PUBLISHED。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AnnouncementScheduleJob {

    /** announcement服务。 */
    private final AnnouncementService announcementService;

    /**
     * 创建AnnouncementScheduleJob。
     *
     * @param announcementService announcement服务
     */
    public AnnouncementScheduleJob(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    /**
     * 执行publishDueAnnouncements。
     */
    @Scheduled(cron = "0 */1 * * * *")
    @SchedulerLock(name = "AnnouncementScheduleJob.publishDue", lockAtLeastFor = "PT30S", lockAtMostFor = "PT5M")
    public void publishDueAnnouncements() {
        announcementService.publishDueScheduled();
    }
}
