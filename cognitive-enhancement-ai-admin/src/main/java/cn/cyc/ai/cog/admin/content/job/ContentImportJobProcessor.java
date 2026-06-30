package cn.cyc.ai.cog.admin.content.job;

import cn.cyc.ai.cog.platform.knowledge.service.ContentImportJobService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 内容导入异步 worker：轮询 PENDING 任务并推进状态。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class ContentImportJobProcessor {

    /** 内容ImportJob服务。 */
    private final ContentImportJobService contentImportJobService;

    /**
     * 创建ContentImportJobProcessor。
     *
     * @param contentImportJobService 内容ImportJob服务
     */
    public ContentImportJobProcessor(ContentImportJobService contentImportJobService) {
        this.contentImportJobService = contentImportJobService;
    }

    /**
     * 执行pollPendingJobs。
     */
    @Scheduled(fixedDelayString = "${cog.admin.content-import.poll-interval-ms:30000}")
    @SchedulerLock(name = "ContentImportJobProcessor.poll", lockAtLeastFor = "PT5S", lockAtMostFor = "PT5M")
    public void pollPendingJobs() {
        contentImportJobService.processNextPendingJob();
    }
}
