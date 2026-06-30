package cn.cyc.ai.cog.platform.importtask.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.importtask.entity.ImportTaskEntity;
import cn.cyc.ai.cog.platform.importtask.spi.ImportTaskPersistencePort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = false)
public class InMemoryImportTaskPersistenceService implements ImportTaskPersistencePort {

    private final AtomicLong seq = new AtomicLong(1);
    private final Map<Long, ImportTaskEntity> tasks = new ConcurrentHashMap<>();

    @Override
    public ImportTaskEntity save(ImportTaskEntity task) {
        if (task.getId() == null) {
            task.setId(seq.getAndIncrement());
        }
        LocalDateTime now = LocalDateTime.now();
        task.setCreateTime(now);
        task.setUpdateTime(now);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void update(ImportTaskEntity task) {
        task.setUpdateTime(LocalDateTime.now());
        tasks.put(task.getId(), task);
    }

    @Override
    public Optional<ImportTaskEntity> findByCode(Long tenantId, Long userId, String taskCode) {
        return tasks.values().stream()
                .filter(t -> tenantId.equals(t.getTenantId())
                        && userId.equals(t.getUserId())
                        && taskCode.equals(t.getTaskCode()))
                .findFirst();
    }

    @Override
    public Optional<ImportTaskEntity> findById(Long tenantId, Long userId, Long id) {
        ImportTaskEntity entity = tasks.get(id);
        if (entity == null || !tenantId.equals(entity.getTenantId()) || !userId.equals(entity.getUserId())) {
            return Optional.empty();
        }
        return Optional.of(entity);
    }

    @Override
    public PageResult<ImportTaskEntity> page(Long tenantId, Long userId, long current, long size) {
        List<ImportTaskEntity> all = tasks.values().stream()
                .filter(t -> tenantId.equals(t.getTenantId()) && userId.equals(t.getUserId()))
                .sorted(Comparator.comparing(ImportTaskEntity::getCreateTime).reversed())
                .toList();
        long page = current < 1 ? 1 : current;
        long pageSize = size < 1 ? 10 : size;
        int from = (int) ((page - 1) * pageSize);
        if (from >= all.size()) {
            return PageResult.empty(page, pageSize);
        }
        int to = (int) Math.min(from + pageSize, all.size());
        return PageResult.of(new ArrayList<>(all.subList(from, to)), all.size(), page, pageSize);
    }

    @Override
    public Optional<ImportTaskEntity> findLatestActive(Long tenantId, Long userId) {
        return tasks.values().stream()
                .filter(t -> tenantId.equals(t.getTenantId()) && userId.equals(t.getUserId()))
                .filter(t -> "pending".equals(t.getStatus()) || "processing".equals(t.getStatus()))
                .max(Comparator.comparing(ImportTaskEntity::getUpdateTime));
    }
}
