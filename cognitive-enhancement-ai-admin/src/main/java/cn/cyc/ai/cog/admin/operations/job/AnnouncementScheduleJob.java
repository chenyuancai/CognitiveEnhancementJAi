package cn.cyc.ai.cog.admin.operations.job;

import cn.cyc.ai.cog.platform.operations.service.AnnouncementService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 公告定时发布任务：DRAFT 且 publish_at 到期 → PUBLISHED。
 */
@Component
public class AnnouncementScheduleJob {

    private final AnnouncementService announcementService;

    public AnnouncementScheduleJob(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @Scheduled(cron = "0 */1 * * * *")
    @SchedulerLock(name = "AnnouncementScheduleJob.publishDue", lockAtLeastFor = "PT30S", lockAtMostFor = "PT5M")
    public void publishDueAnnouncements() {
        announcementService.publishDueScheduled();
    }
}
