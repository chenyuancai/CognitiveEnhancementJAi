package cn.cyc.ai.cog.app.importtask.support;

import cn.cyc.ai.cog.app.importtask.service.AppImportWorkflowBusinessService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 导入任务异步触发器（委托真实工作流）。
 */
@Component
public class AppImportTaskExecutor {

    private final AppImportWorkflowBusinessService workflowBusinessService;
    private final AppImportProgressPublisher progressPublisher;

    public AppImportTaskExecutor(AppImportWorkflowBusinessService workflowBusinessService,
                                 AppImportProgressPublisher progressPublisher) {
        this.workflowBusinessService = workflowBusinessService;
        this.progressPublisher = progressPublisher;
    }

    @Async
    public void runPipeline(Long tenantId, Long userId, String taskCode) {
        workflowBusinessService.execute(tenantId, userId, taskCode,
                event -> progressPublisher.publish(taskCode, event));
    }
}
