package cn.cyc.ai.cog.admin.content.job;

import cn.cyc.ai.cog.platform.knowledge.service.ContentImportJobService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 内容导入异步 worker：轮询 PENDING 任务并推进状态。
 */
@Component
public class ContentImportJobProcessor {

    private final ContentImportJobService contentImportJobService;

    public ContentImportJobProcessor(ContentImportJobService contentImportJobService) {
        this.contentImportJobService = contentImportJobService;
    }

    @Scheduled(fixedDelayString = "${cog.admin.content-import.poll-interval-ms:30000}")
    @SchedulerLock(name = "ContentImportJobProcessor.poll", lockAtLeastFor = "PT5S", lockAtMostFor = "PT5M")
    public void pollPendingJobs() {
        contentImportJobService.processNextPendingJob();
    }
}
