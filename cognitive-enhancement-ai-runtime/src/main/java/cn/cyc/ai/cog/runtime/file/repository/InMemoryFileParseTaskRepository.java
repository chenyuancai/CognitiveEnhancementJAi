package cn.cyc.ai.cog.runtime.file.repository;

import cn.cyc.ai.cog.runtime.file.domain.FileParseTask;
import cn.cyc.ai.cog.runtime.file.domain.FileParseTaskStatus;
import cn.cyc.ai.cog.runtime.file.spi.FileParseTaskRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 内存文件解析任务仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryFileParseTaskRepository implements FileParseTaskRepository {

    /**
     * 内存解析任务容器。
     */
    private final CopyOnWriteArrayList<FileParseTask> tasks = new CopyOnWriteArrayList<>();

    /**
     * 执行save。
     *
     * @param task task
     */
    @Override
    public void save(FileParseTask task) {
        tasks.add(task);
    }

    /**
     * 查找LatestSucceeded人文件ID。
     *
     * @param fileId 文件ID
     * @return 查找结果
     */
    @Override
    public Optional<FileParseTask> findLatestSucceededByFileId(String fileId) {
        String tenantCode = TenantContext.currentTenantCode();
        return tasks.stream()
                .filter(task -> tenantCode.equals(task.tenantCode()))
                .filter(task -> fileId.equals(task.fileId()))
                .filter(task -> task.status() == FileParseTaskStatus.SUCCEEDED)
                .max(Comparator.comparing(FileParseTask::finishedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(FileParseTask::recordedAt));
    }
}
